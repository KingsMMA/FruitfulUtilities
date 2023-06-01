package dev.kingrabbit.fruitfulutilities.config.properties;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigBoolean {

    String id();
    String display();
    String description();

}
