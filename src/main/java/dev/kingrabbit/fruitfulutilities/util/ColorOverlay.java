package dev.kingrabbit.fruitfulutilities.util;

import java.awt.Color;

public class ColorOverlay {
    public static Color overlayColors(Color colorA, Color colorB, double opacity) {
        return new Color(
                (int) (colorA.getRed() * (1 - opacity) + colorB.getRed() * opacity),
                (int) (colorA.getGreen() * (1 - opacity) + colorB.getGreen() * opacity),
                (int) (colorA.getBlue() * (1 - opacity) + colorB.getBlue() * opacity)
        );
    }

}