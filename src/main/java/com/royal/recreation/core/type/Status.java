package com.royal.recreation.core.type;

public enum Status {

    ACTIVE("可用"),
    DEL("停用");

    private String desc;

    Status(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}