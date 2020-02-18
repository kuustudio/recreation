package com.royal.recreation.spring.mongo.basic;

import java.util.ArrayList;
import java.util.List;


public class RegexUtils {

    public static final List<String> special_list = new ArrayList<String>();

    static {
        special_list.add("\\");
        special_list.add("$");
        special_list.add("(");
        special_list.add(")");
        special_list.add("*");
        special_list.add("+");
        special_list.add(".");
        special_list.add("[");
        special_list.add("]");
        special_list.add("^");
        special_list.add("{");
        special_list.add("}");
        special_list.add("|");
        special_list.add("?");
    }


    public static String compile(String keyword) {
        for (String s : special_list) {
            if (keyword.contains(s)) {
                keyword = keyword.replaceAll("\\" + s, "\\\\" + s);
            }
        }
        return keyword;
    }
}
