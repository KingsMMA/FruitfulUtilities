package dev.kingrabbit.fruitfulutilities.util;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    public static String toFancyNumber(long num) {
        return NumberFormat.getInstance(Locale.US).format(num);
    }

}
