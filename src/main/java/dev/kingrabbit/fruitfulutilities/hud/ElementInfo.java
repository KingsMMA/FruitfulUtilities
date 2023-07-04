package dev.kingrabbit.fruitfulutilities.hud;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ElementInfo {

    String id();

}
