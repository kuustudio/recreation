package com.royal.recreation.core.type;

/**
 * 积分记录类型
 */
public enum PointRecordType {

    RECHARGE("账户上分"),
    CASH_OUT("账户提现"),
    BET("投注扣款"),
    CANCEL_BET("撤单返款"),
    FAN_DIAN("游戏返点"),
    AWARD("消费佣金"),
    BONUS("奖金派送");


    private String desc;

    PointRecordType(String desc) {
        this.desc = desc;
    }

    public String getCode() {
        return name();
    }

    public String getDesc() {
        return desc;
    }

}
