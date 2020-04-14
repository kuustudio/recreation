package com.royal.recreation.controller;

import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class PostCodeQuery {

    private JSONObject para;
    private List<JSONObject> code;
    private Map<String, BigDecimal> bonusLimitType;

    @Data
    public static class Code {
        private Integer mode;
        private Integer beiShu;
        private String actionData;
        private Integer actionNum;
        private Integer playedGroup;
        private Integer playedId;
        private String bonusProp;
    }

    @Data
    public static class Para {
        private Integer type;
        private Long actionNo;
        private String kjTime;
    }

}
