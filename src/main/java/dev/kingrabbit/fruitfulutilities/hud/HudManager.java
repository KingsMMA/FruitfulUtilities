package dev.kingrabbit.fruitfulutilities.hud;

import com.google.gson.JsonObject;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.hud.elements.AuctionTimerElement;
import dev.kingrabbit.fruitfulutilities.hud.elements.SearchingTrackerElement;
import dev.kingrabbit.fruitfulutilities.hud.elements.TrackedUpgradesElement;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class HudManager {

    public final LinkedHashMap<HudElement, ElementInfo> elementList;
    public final Map<String, HudElement> elementIds;
    public final Map<Class<? extends HudElement>, HudElement> elementMap;

    @SuppressWarnings("unchecked")
    public HudManager() {
        elementList = new LinkedHashMap<>();
        elementIds = new HashMap<>();
        elementMap = new HashMap<>();

        for (Class<? extends HudElement> elementClass : new Class[]{
                AuctionTimerElement.class, TrackedUpgradesElement.class, SearchingTrackerElement.class
        }) {
            try {
                HudElement category = elementClass.getDeclaredConstructor().newInstance();
                ElementInfo annotation = elementClass.getAnnotation(ElementInfo.class);
                elementList.put(category, annotation);
                elementIds.put(annotation.id(), category);
                elementMap.put(category.getClass(), category);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        load();
    }

    @SuppressWarnings("unused")
    public <T extends HudElement> T getElement(Class<T> element) {
        return element.cast(elementMap.get(element));
    }

    public void load() {
        Path fabricHudDirectory = FabricLoader.getInstance().getConfigDir();
        Path configPath = fabricHudDirectory.resolve("fruitful_utilities_hud.json");
        try {
            BufferedReader reader = Files.newBufferedReader(configPath);
            JsonObject data = FruitfulUtilities.GSON.fromJson(reader, JsonObject.class);
            for (String elementId : data.keySet()) {
                if (!elementIds.containsKey(elementId)) continue;
                HudElement element = elementIds.get(elementId);
                JsonObject section = data.getAsJsonObject(elementId);
                Class<? extends HudElement> elementClass = element.getClass();
                for (Field field : elementClass.getFields()) {
                    if (field.isAnnotationPresent(Serializable.class)) {
                        Serializable annotation = field.getAnnotation(Serializable.class);
                        String id = annotation.id();
                        if (section.has(id)) {
                            field.set(element, FruitfulUtilities.GSON.fromJson(section.get(id), field.getType()));
                        }
                    }
                }
            }
        } catch (IOException exception) {
            FruitfulUtilities.LOGGER.error("An error occurred loading the HUD data", exception);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public void save() {
        JsonObject configData = new JsonObject();
        for (String elementId : elementIds.keySet()) {
            HudElement element = elementIds.get(elementId);
            JsonObject elementData = new JsonObject();

            for (Field field : element.getClass().getFields()) {
                if (field.isAnnotationPresent(Serializable.class)) {
                    try {
                        elementData.add(field.getAnnotation(Serializable.class).id(), FruitfulUtilities.GSON.toJsonTree(field.get(element)));
                    } catch (IllegalAccessException exception) {
                        FruitfulUtilities.LOGGER.error("Unable to save hud data", exception);
                        FruitfulUtilities.LOGGER.error("Field: " + field.getName());
                        FruitfulUtilities.LOGGER.error("Element: " + element.getClass().getName());
                    }
                }
            }

            configData.add(elementId, elementData);
        }

        Path fabricHudDirectory = FabricLoader.getInstance().getConfigDir();
        try {
            Files.createDirectories(fabricHudDirectory);
            BufferedWriter writer = Files.newBufferedWriter(fabricHudDirectory.resolve("fruitful_utilities_hud.json"));
            FruitfulUtilities.GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException exception) {
            FruitfulUtilities.LOGGER.error("An error occurred saving the HUD data", exception);
        }
    }

}
