package org.prometheus.utils;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

    public static final boolean isNotEmpty(Object o) {
        return !isEmpty(o);
    }

    public static final boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        }

        if (o != null && o instanceof String[]) {
            return ((String[]) o).length == 0;
        }

        if (o != null && o instanceof String) {
            return ((String) o).trim().equals("");
        }

        if (o != null && o instanceof List<?>) {
            return ((List<?>) o).isEmpty();
        }

        if (o != null && o instanceof Set<?>) {
            return ((Set<?>) o).isEmpty();
        }

        if (o != null && o instanceof Collection<?>) {
            return ((Collection<?>) o).isEmpty();
        }

        if (o != null && o instanceof Map<?, ?>) {
            return ((Map<?, ?>) o).isEmpty();
        }

        return false;
    }

    public static void main(String[] args) {
        System.out.println(Utils.isEmpty(new String[] { "A" }));
    }
}
