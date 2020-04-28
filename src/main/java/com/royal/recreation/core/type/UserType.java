package com.royal.recreation.core.type;

public enum UserType {

    ADMIN("管理员"),
    MANAGE("总代理"),
    AGENT("代理"),
    MEMBER("会员");

    private final String desc;

    UserType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

}
