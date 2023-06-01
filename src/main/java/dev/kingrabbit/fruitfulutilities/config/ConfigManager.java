package dev.kingrabbit.fruitfulutilities.config;

import dev.kingrabbit.fruitfulutilities.config.categories.GeneralCategory;
import dev.kingrabbit.fruitfulutilities.config.categories.TabCategory;

import java.lang.reflect.InvocationTargetException;
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

        for (Class<? extends ConfigCategory> categoryClass : new Class[]{GeneralCategory.class, TabCategory.class}) {
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

    }

    public <T extends ConfigCategory> T getCategory(Class<T> category) {
        return category.cast(categoryMap.get(category));
    }

}
