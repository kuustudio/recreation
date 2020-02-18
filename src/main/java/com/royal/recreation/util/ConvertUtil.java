package com.royal.recreation.util;

import java.math.BigDecimal;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConvertUtil {
    public static String String(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        } else {
            return String.valueOf(obj);
        }
    }

    public static String String(Object obj, String defaultValue) {
        if (obj == null) {
            return defaultValue;
        } else {
            return String.valueOf(obj);
        }
    }

    public static Long formatLong(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        } else {
            return Long.valueOf(String.valueOf(obj));
        }
    }

    public static BigDecimal formatBigDecimimal(Object obj) {
        if (obj == null || "".equals(obj)) {
            return null;
        } else {
            return new BigDecimal((String) String.valueOf(obj));
        }
    }

    public static Long formatLong(Object obj, long value) {
        if (obj == null || "".equals(obj)) {
            return value;
        } else {
            return Long.valueOf(String.valueOf(obj));
        }
    }


    public static int Int(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return Integer.parseInt(String.valueOf(obj));
        }
    }

    public static Integer IntNull(Object obj) {
        try {
            if (obj == null || "".equals(obj)) {
                return null;
            } else {
                return Integer.valueOf(String.valueOf(obj));
            }
        } catch (Exception e) {
            return null;
        }
    }

    public static int Int(Object obj, int value) {
        if (obj == null) {
            return value;
        } else {
            return Integer.parseInt(String.valueOf(obj));
        }
    }

    public static Integer Integer(Object obj) {
        if (obj == null || obj.equals("null")) {
            return null;
        } else {
            return Integer.valueOf(String.valueOf(obj));
        }
    }

    public static Boolean Boolean(Object obj) {
        if (obj == null || obj.equals("null")) {
            return null;
        } else {
            return Boolean.valueOf(String.valueOf(obj));
        }
    }

    public static Boolean Boolean(Object obj, boolean value) {
        if (obj == null || obj.equals("null")) {
            return value;
        } else {
            return Boolean.valueOf(String.valueOf(obj));
        }
    }

    public static Double Double(Object obj) {
        if (obj == null || obj.equals("null")) {
            return null;
        } else {
            try {
                return Double.valueOf(String.valueOf(obj));
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }


    public static Double Double(String obj) {
        if (obj == null || obj.equals("null")) {
            return null;
        } else {
            try {
                return Double.valueOf(obj);
            } catch (NumberFormatException e) {
                return null;
            }
        }
    }

    public static Double Double(Object obj, double value) {
        if (obj == null || obj.equals("null")) {
            return value;
        } else {
            try {
                return Double.valueOf(String.valueOf(obj));
            } catch (NumberFormatException e) {
                return value;
            }
        }
    }

    public static Map map(Object obj, Map value) {
        if (obj == null || obj.equals("null")) {
            return value;
        } else {
            try {
                return (Map) obj;
            } catch (Exception e) {
                return value;
            }
        }
    }

    public static String filterEmoji(String source) {
        if (source != null) {
            Pattern emoji = Pattern.compile("[\ud83c\udc00-\ud83c\udfff]|[\ud83d\udc00-\ud83d\udfff]|[\u2600-\u27ff]", Pattern.UNICODE_CASE | Pattern.CASE_INSENSITIVE);
            Matcher emojiMatcher = emoji.matcher(source);
            if (emojiMatcher.find()) {
                source = emojiMatcher.replaceAll("*");
                return source;
            }
            return source;
        }
        return null;
    }

}
