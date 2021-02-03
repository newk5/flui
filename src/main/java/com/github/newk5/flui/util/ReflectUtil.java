package com.github.newk5.flui.util;

import java.lang.reflect.Field;

public abstract class ReflectUtil {

    public static Object getValue(Object obj, Field field) {

        Object value = "";
        try {

            field.setAccessible(true);
            if (field != null) {
                Object v = field.get(obj);
                value = v != null ? v : "";
            }
        } catch (Exception ex) {
        }
        return value;
    }

    public static void setValue(Object obj, Field field, Object val) {

        try {

            field.setAccessible(true);
            if (field != null) {
                field.set(obj, val);

            }
        } catch (Exception ex) {

        }

    }

}
