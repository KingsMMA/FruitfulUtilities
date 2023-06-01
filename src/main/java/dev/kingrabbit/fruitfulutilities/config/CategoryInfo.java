package dev.kingrabbit.fruitfulutilities.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface CategoryInfo {

    String id();
    String display();

}
