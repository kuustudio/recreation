package com.royal.recreation.core.type;

public enum UserType {

    ADMIN("管理员"),
    AGENT("代理"),
    MEMBER("会员");

    private String desc;

    UserType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
