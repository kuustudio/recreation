package com.royal.recreation.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class MapUtil {

    public static Map<String, Object> objectToMap(Object obj)  {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
            }
            map.put(fieldName, value);
        }
        return map;
    }

}
