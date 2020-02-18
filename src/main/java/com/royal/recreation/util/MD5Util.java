package com.royal.recreation.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

/**
 *
 */
public class MD5Util {

    private static final String HEX_NUMS_STR = "0123456789ABCDEF";

    public static String byteToHexString(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte i : bytes) {
            String hex = Integer.toHexString(i & 0xFF);
            if (hex.length() == 1) {
                hex = '0' + hex;
            }
            hexString.append(hex.toUpperCase());
        }
        return hexString.toString();
    }

    public static byte[] hexStringToByte(String hex) {
        int len = hex.length() / 2;
        byte[] result = new byte[len];
        char[] hexChars = hex.toCharArray();
        for (int i = 0; i < len; i++) {
            int pos = i * 2;
            result[i] = (byte) (HEX_NUMS_STR.indexOf(hexChars[pos]) << 4 | HEX_NUMS_STR.indexOf(hexChars[pos + 1]));
        }
        return result;
    }

    /*** 
     *
     */
    public static String string2MD5(String inStr) {
        return string2MD5(inStr, null);
    }

    /**
     * 将字符串转为MD5
     *
     * @param inStr 要加密的字符串
     * @param salt  盐
     * @return
     */
    public static String string2MD5(String inStr, byte[] salt) {
        MessageDigest md5;
        try {
            // 声明消息摘要对象
            md5 = MessageDigest.getInstance("MD5");
            if (salt != null) {
                // 将盐数据传入消息摘要对象
                md5.update(salt);
            }
            // 将口令的数据传给消息摘要对象
            md5.update(inStr.getBytes(StandardCharsets.UTF_8));
            // 获得消息摘要的字节数组
            byte[] digest = md5.digest();
            return byteToHexString(digest);
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
    }


    /**
     *
     */
    public static String convertMD5(String inStr) {

        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        String s = new String(a);
        return s;

    }

}