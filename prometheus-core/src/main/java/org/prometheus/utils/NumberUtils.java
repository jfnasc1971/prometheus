package org.prometheus.utils;

import java.text.NumberFormat;
import java.util.Locale;

public class NumberUtils {

    private static NumberFormat nf = null;
    
    static {
        nf = NumberFormat.getInstance(new Locale("pt", "BR"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(2);
    }
    
    public static final String format(Number n) {
        return nf.format(n.doubleValue());
    }

    public static void main(String[] args) {
        System.out.println(NumberUtils.format(Double.valueOf("9.313225746154785E-10")));
    }
}
