package dev.kingrabbit.fruitfulutilities.util;

import java.awt.Color;

public class ColorOverlay {
    public static Color overlayColors(Color colorA, Color colorB, double opacity) {
        // Extract the RGB components of colorA
        int redA = colorA.getRed();
        int greenA = colorA.getGreen();
        int blueA = colorA.getBlue();

        // Extract the RGB components of colorB
        int redB = colorB.getRed();
        int greenB = colorB.getGreen();
        int blueB = colorB.getBlue();

        // Calculate the resulting RGB components
        int redResult = (int) (redA * (1 - opacity) + redB * opacity);
        int greenResult = (int) (greenA * (1 - opacity) + greenB * opacity);
        int blueResult = (int) (blueA * (1 - opacity) + blueB * opacity);

        // Create and return the resulting color
        return new Color(redResult, greenResult, blueResult);
    }

}