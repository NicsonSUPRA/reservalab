package com.uespi.reservalab.utils;

import java.util.Collection;
import java.util.Map;

public class Utils {
    /**
     * 
     *
     * @param obj
     * @return
     */
    public static boolean isNotEmpty(Object obj) {
        if (obj == null) {
            return false;
        }

        if (obj instanceof String) {
            return !((String) obj).trim().isEmpty();
        }

        if (obj instanceof Collection) {
            return !((Collection<?>) obj).isEmpty();
        }

        if (obj instanceof Map) {
            return !((Map<?, ?>) obj).isEmpty();
        }

        if (obj.getClass().isArray()) {
            return ((Object[]) obj).length > 0;
        }

        // Outros tipos de objeto são considerados "não vazios" por padrão
        return true;
    }

    public static boolean isEmpty(Object obj) {
        return !isNotEmpty(obj);
    }

}
