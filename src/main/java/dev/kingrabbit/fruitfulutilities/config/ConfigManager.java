package dev.kingrabbit.fruitfulutilities.config;

import com.google.gson.JsonObject;
import dev.kingrabbit.fruitfulutilities.FruitfulUtilities;
import dev.kingrabbit.fruitfulutilities.config.categories.*;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigBoolean;
import dev.kingrabbit.fruitfulutilities.config.properties.ConfigDropdown;
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

public class ConfigManager {

    public final LinkedHashMap<ConfigCategory, CategoryInfo> categoryList;
    public final Map<String, ConfigCategory> categoryIds;
    public final Map<Class<? extends ConfigCategory>, ConfigCategory> categoryMap;

    public ConfigManager() {
        categoryList = new LinkedHashMap<>();
        categoryIds = new HashMap<>();
        categoryMap = new HashMap<>();

        for (Class<? extends ConfigCategory> categoryClass : new Class[] {
                GeneralCategory.class, PathViewerCategory.class, CodeHiderCategory.class, SearchingTrackerCategory.class, MessageHiderCategory.class, TabCategory.class
        }) {

            try {
                ConfigCategory category = categoryClass.getDeclaredConstructor().newInstance();
                CategoryInfo annotation = categoryClass.getAnnotation(CategoryInfo.class);
                categoryList.put(category, annotation);
                categoryIds.put(annotation.id(), category);
                categoryMap.put(category.getClass(), category);
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        load();
    }

    public <T extends ConfigCategory> T getCategory(Class<T> category) {
        return category.cast(categoryMap.get(category));
    }

    public boolean enabled() {
        GeneralCategory generalCategory = getCategory(GeneralCategory.class);
        if (!generalCategory.enabled) return false;
        if (generalCategory.gameDetection) return FruitfulUtilities.getInstance().inMelonKing;
        return true;
    }

    public void load() {
        Path fabricConfigDirectory = FabricLoader.getInstance().getConfigDir();
        Path configPath = fabricConfigDirectory.resolve("fruitful_utilities.json");
        try {
            BufferedReader reader = Files.newBufferedReader(configPath);
            JsonObject data = FruitfulUtilities.GSON.fromJson(reader, JsonObject.class);
            for (String categoryId : data.keySet()) {
                ConfigCategory category = categoryIds.get(categoryId);
                JsonObject section = data.getAsJsonObject(categoryId);
                for (Field field : category.getClass().getFields()) {
                    String id = "404";
                    if (field.isAnnotationPresent(ConfigBoolean.class)) id = field.getAnnotation(ConfigBoolean.class).id();
                    else if (field.isAnnotationPresent(ConfigDropdown.class)) id = field.getAnnotation(ConfigDropdown.class).id();

                    if (section.has(id)) {
                        try {
                            field.set(category, FruitfulUtilities.GSON.fromJson(section.get(id), field.getType()));
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void save() {
        JsonObject configData = new JsonObject();
        for (String categoryId : categoryIds.keySet()) {
            ConfigCategory category = categoryIds.get(categoryId);
            JsonObject categoryData = new JsonObject();

            for (Field field : category.getClass().getFields()) {
                String id = "404";
                if (field.isAnnotationPresent(ConfigBoolean.class)) id = field.getAnnotation(ConfigBoolean.class).id();
                else if (field.isAnnotationPresent(ConfigDropdown.class)) id = field.getAnnotation(ConfigDropdown.class).id();

                try {
                    categoryData.add(id, FruitfulUtilities.GSON.toJsonTree(field.get(category)));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    System.out.println(field.getName());
                    System.out.println(category.getClass().getName());
                }
            }

            configData.add(categoryId, categoryData);
        }

        Path fabricConfigDirectory = FabricLoader.getInstance().getConfigDir();
        try {
            Files.createDirectories(fabricConfigDirectory);
            BufferedWriter writer = Files.newBufferedWriter(fabricConfigDirectory.resolve("fruitful_utilities.json"));
            FruitfulUtilities.GSON.toJson(configData, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
