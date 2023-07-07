package dev.kingrabbit.fruitfulutilities.pathviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.PathViewerCategory;
import dev.kingrabbit.fruitfulutilities.util.NumberUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class PathManager {

    public static final HashMap<String, String> pathParents = new HashMap<>();
    public static final ArrayList<String> purchasedIds = new ArrayList<>();
    public static final HashMap<String, JsonObject> paths = new HashMap<>();
    public static final HashMap<JsonObject, String> upgradeToId = new HashMap<>();
    public static final HashMap<JsonObject, String> upgradeToPath = new HashMap<>();
    public static final ArrayList<JsonObject> tracking = new ArrayList<>();

    static {
        pathParents.put("urban", "beginnings");
        pathParents.put("true_urban", "urban");
        pathParents.put("science", "urban");
        pathParents.put("democracy", "urban");

        pathParents.put("religion", "beginnings");
    }

    public static void loadPaths() {
        paths.clear();
        loadPath("beginnings", "upgrades");
        loadPath("religion", "religion");
        loadPath("urban", "urban");
        loadPath("true_urban", "true_urban");

        for (String pathId : paths.keySet()) {
            JsonObject path = paths.get(pathId);
            for (String upgradeId : path.keySet()) {
                JsonObject upgrade = path.getAsJsonObject(upgradeId);
                upgradeToId.put(upgrade, upgradeId);
                upgradeToPath.put(upgrade, pathId);
            }
        }
    }

    public static void loadPath(String localName, String remoteName) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("https://raw.githubusercontent.com/KingsMMA/FruitfulData/main/" + remoteName + ".json").openStream()));
            JsonObject beginnings = FruitfulUtilities.GSON.fromJson(reader, JsonObject.class);
            paths.put(localName, beginnings);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getId(JsonObject upgrade) {
        if (upgradeToId.containsKey(upgrade)) return upgradeToId.get(upgrade);
        FruitfulUtilities.LOGGER.warn("Couldn't get ID for upgrade with name \"" + upgrade.get("display").getAsString() + "\".");
        return null;
    }

    public static void unlocked(String upgradeName, String description) {
        for (JsonObject path : paths.values()) {
            for (String upgradeId : path.keySet()) {
                JsonObject upgrade = path.getAsJsonObject(upgradeId);
                if (upgrade.get("display").getAsString().equals(upgradeName)) {
                    if (upgrade.has("has_duplicate_names") && upgrade.get("has_duplicates_names").getAsBoolean()) {
                        if (!Objects.equals(description, upgrade.get("description").getAsString()))
                            continue;
                    }
                    purchasedIds.add(getId(upgrade));
                    if (upgrade.has("path")) {
                        String newPath = upgrade.get("path").getAsString();
                        if (!PathScreen.sections.containsKey(newPath))
                            PathScreen.sections.put(newPath, new float[]{-19284, -64, 1});
                    }
                    return;
                }
            }
        }
        FruitfulUtilities.LOGGER.warn("Unable to find upgrade with name \"" + upgradeName + "\".");
    }

    public static List<JsonObject> requiredToUnlock(JsonObject upgrade) {
        return requiredToUnlock(upgrade, true, true);
    }

    public static List<JsonObject> requiredToUnlock(JsonObject upgrade, boolean includeUpgrade, boolean disableIfNotCumulative) {
        List<JsonObject> allRequired = new ArrayList<>();

        String upgradeId = getId(upgrade);
        if (purchasedIds.contains(upgradeId)) return allRequired;

        if (includeUpgrade) allRequired.add(upgrade);

        if (disableIfNotCumulative) {
            PathViewerCategory category = FruitfulUtilities.getInstance().configManager.getCategory(PathViewerCategory.class);
            if (!category.cumulative) return allRequired;
        }

        if (upgrade.has("requires")) {
            String path = upgradeToPath.get(upgrade);
            JsonArray requires = upgrade.getAsJsonArray("requires");
            for (JsonElement _requiredId : requires) {
                String requiredId = _requiredId.getAsString();
                JsonObject required = findUpgrade(path, requiredId);
                if (required != null) {
                    if (purchasedIds.contains(getId(required))) continue;
                    List<JsonObject> requiredPath = requiredToUnlock(required);
                    for (JsonObject req : requiredPath)
                        if (!allRequired.contains(req))
                            allRequired.add(req);
                }
            }
        }

        return allRequired;
    }

    public static HashMap<Currency, Integer> cumulativePrice(List<JsonObject> upgrades) {
        HashMap<Currency, Integer> totalPrice = new HashMap<>();

        for (JsonObject upgrade : upgrades) {
            String currencyRaw = upgrade.get("currency").getAsString();
            try {
                Currency currency = Currency.valueOf(currencyRaw.toUpperCase());
                int price = upgrade.get("price").getAsInt();
                if (totalPrice.containsKey(currency)) totalPrice.put(currency, totalPrice.get(currency) + price);
                else totalPrice.put(currency, price);
            } catch (IllegalArgumentException exception) {
                FruitfulUtilities.LOGGER.error("Unknown currency: " + currencyRaw, exception);
            }
        }

        return totalPrice;
    }

    public static JsonObject findUpgrade(String path, String upgradeId) {
        if (paths.get(path).has(upgradeId)) return paths.get(path).getAsJsonObject(upgradeId);
        if (pathParents.containsKey(path)) return findUpgrade(pathParents.get(path), upgradeId);
        return null;
    }

    public static StringBuilder appendFormattedCost(StringBuilder stringBuilder, HashMap<Currency, Integer> costMap) {
        for (Currency currency : costMap.keySet()) {
            int price = costMap.get(currency);
            stringBuilder.append("§").append(currency.getSecondaryColor()).append(NumberUtils.toFancyNumber(price)).append(" ").append(currency.format(price)).append("§7, ");
        }
        return new StringBuilder(stringBuilder.substring(0, stringBuilder.length() - 2));
    }

    public static boolean locked(JsonObject upgrade) {
        if (purchasedIds.contains(getId(upgrade))) return false;

        if (upgrade.has("path")) {
            String path = upgrade.get("path").getAsString();
            List<String> parents = getParentPaths(path, true);

            for (String unlockedPath : PathScreen.sections.keySet()) {
                if (!parents.contains(unlockedPath)) return true;
            }
        }

        if (!upgrade.has("requires")) return false;
        JsonArray required = upgrade.getAsJsonArray("requires");
        if (required.isEmpty()) return false;
        String path = upgradeToPath.get(upgrade);
        for (JsonElement _requiredUpgradeId : required) {
            String requiredUpgradeId = _requiredUpgradeId.getAsString();
            boolean not = requiredUpgradeId.startsWith("!");
            if (not) requiredUpgradeId = requiredUpgradeId.substring(1);
            JsonObject requiredUpgrade = findUpgrade(path, requiredUpgradeId);
            if (requiredUpgrade == null) continue;
            boolean unlocked = purchasedIds.contains(getId(requiredUpgrade));
            if ((not && unlocked) || (!not && !unlocked)) return true;
        }
        return false;
    }

    public static List<String> getParentPaths(String path, boolean includeOriginal) {
        List<String> parents = new ArrayList<>();
        if (includeOriginal) parents.add(path);

        String parent = pathParents.get(path);
        while (parent != null) {
            parents.add(parent);
            parent = pathParents.get(parent);
        }

        return parents;
    }

}
