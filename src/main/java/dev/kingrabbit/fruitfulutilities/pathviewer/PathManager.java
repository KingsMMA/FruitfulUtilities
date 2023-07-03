package dev.kingrabbit.fruitfulutilities.pathviewer;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class PathManager {

    public static final HashMap<String, String> pathParents = new HashMap<>();
    public static final ArrayList<String> purchased = new ArrayList<>();
    public static final HashMap<String, JsonObject> paths = new HashMap<>();
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

    // TODO ??
    public static void unlocked(String upgradeName) {
        purchased.add(upgradeName);
        for (JsonObject path : paths.values()) {
            for (String upgradeId : path.keySet()) {
                JsonObject upgrade = path.getAsJsonObject(upgradeId);
                if (upgrade.get("display").getAsString().equals(upgradeName)) {
                    if (upgrade.has("path")) {
                        String newPath = upgrade.get("path").getAsString();
                        if (!PathScreen.sections.containsKey(newPath))
                            PathScreen.sections.put(newPath, new float[]{-19284, -64, 1});
                    }
                }
            }
        }
    }

    public static List<JsonObject> requiredToUnlock(JsonObject upgrade) {
        return requiredToUnlock(upgrade, true);
    }

    public static List<JsonObject> requiredToUnlock(JsonObject upgrade, boolean includeUpgrade) {
        List<JsonObject> allRequired = new ArrayList<>();

        if (includeUpgrade) allRequired.add(upgrade);

        if (upgrade.has("requires")) {
            String path = upgradeToPath.get(upgrade);

            JsonArray requires = upgrade.getAsJsonArray("requires");
            for (JsonElement _requiredId : requires) {
                String requiredId = _requiredId.getAsString();
                JsonObject required = findUpgrade(path, requiredId);
                if (required != null) {
                    List<JsonObject> requiredPath = requiredToUnlock(required);
                    for (JsonObject req : requiredPath)
                        if (!allRequired.contains(req))
                            allRequired.add(req);
                }
            }
        }

        return allRequired;
    }

    public static HashMap<String, Integer> cumulativePrice(List<JsonObject> upgrades) {
        HashMap<String, Integer> totalPrice = new HashMap<>();

        for (JsonObject upgrade : upgrades) {
            String currency = upgrade.get("currency").getAsString();
            int price = upgrade.get("price").getAsInt();
            if (totalPrice.containsKey(currency)) totalPrice.put(currency, totalPrice.get(currency) + price);
            else totalPrice.put(currency, price);
        }

        return totalPrice;
    }

    public static JsonObject findUpgrade(String path, String upgradeId) {
        if (paths.get(path).has(upgradeId)) return paths.get(path).getAsJsonObject(upgradeId);
        if (pathParents.containsKey(path)) return findUpgrade(pathParents.get(path), upgradeId);
        return null;
    }

}
