package dev.kingrabbit.fruitfulutilities.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IncompatibleMod {

    String modId();
    String description();

}
