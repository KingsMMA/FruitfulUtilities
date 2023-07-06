package dev.kingrabbit.fruitfulutilities.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    public static String toFancyNumber(int num) {
        return NumberFormat.getInstance(Locale.US).format((Integer) num);
    }

}
