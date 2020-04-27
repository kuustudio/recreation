package com.royal.recreation.core.type;

import com.royal.recreation.util.Constant;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public enum BonusLimitType {

    L_1(1, "豹子", BigDecimal.valueOf(5_0000)),
    L_2(2, "顺子", BigDecimal.valueOf(5_0000)),
    L_3(3, "大", BigDecimal.valueOf(10_0000)),
    L_4(4, "小", BigDecimal.valueOf(10_0000)),
    L_5(5, "单", BigDecimal.valueOf(10_0000)),
    L_6(6, "双", BigDecimal.valueOf(10_0000)),
    L_21(21, "猜数字", BigDecimal.valueOf(10_0000)),
    L_7(7, "二码定位", BigDecimal.valueOf(3_0000)),
    L_8(8, "三码定位", BigDecimal.valueOf(3_0000)),
    L_9(9, "二码不定位", BigDecimal.valueOf(5_0000)),
    L_10(10, "三码不定位", BigDecimal.valueOf(5_0000)),
    L_11(11, "一帆风顺", BigDecimal.valueOf(5_0000)),
    L_12(12, "好事成双", BigDecimal.valueOf(5_0000)),
    L_13(13, "三星报喜", BigDecimal.valueOf(5_0000)),
    L_14(14, "龙", BigDecimal.valueOf(100000)),
    L_15(15, "虎", BigDecimal.valueOf(100000)),
    L_16(16, "和", BigDecimal.valueOf(100000)),
    L_17(17, "对子", BigDecimal.valueOf(5_0000)),
    L_18(18, "半顺", BigDecimal.valueOf(5_0000)),
    L_19(19, "杂六", BigDecimal.valueOf(5_0000)),
    L_ALL(20, "全部", BigDecimal.valueOf(20_0000)),

    ;
    private static final Map<Integer, BonusLimitType> DATA;

    static {
        DATA = new HashMap<>();
        for (BonusLimitType value : BonusLimitType.values()) {
            DATA.put(value.id, value);
        }
    }


    private final int id;
    private final String name;
    private final BigDecimal limit;

    BonusLimitType(int id, String name, BigDecimal limit) {
        this.id = id;
        this.name = name;
        this.limit = limit;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public BigDecimal getRealLimit() {
        return Constant.SYSTEM_LIMIT.get(this);
    }

    public static BonusLimitType find(Integer id) {
        return DATA.get(id);
    }

    public static BonusLimitType parse(String actionData) {
        switch (actionData) {
            case "大":
            case "总和大":
                return L_3;
            case "小":
            case "总和小":
                return L_4;
            case "单":
            case "总和单":
                return L_5;
            case "双":
            case "总和双":
                return L_6;
            case "龙":
                return L_14;
            case "虎":
                return L_15;
            case "和":
                return L_16;
            case "豹子":
                return L_1;
            case "顺子":
                return L_2;
            case "对子":
                return L_17;
            case "半顺":
                return L_18;
            case "杂六":
                return L_19;

        }
        return null;
    }

}