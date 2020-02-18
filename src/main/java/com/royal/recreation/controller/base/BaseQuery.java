package com.royal.recreation.controller.base;

import lombok.Data;

@Data
public class BaseQuery {

    public static final BaseQuery EMPTY_QUERY = new BaseQuery();

    private String agentName;
    private String username;
    private String fromDate;
    private String toDate;
    private String fromTime;
    private String toTime;
    private int page = 1;
    private int pageType = 0;

    public int getPage() {
        return page + pageType;
    }
}
