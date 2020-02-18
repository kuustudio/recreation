package com.royal.recreation.vo;

import lombok.Data;

@Data
public class Test {


    /**
     * lastNo : {"actionNo":50641169,"actionTime":"11:19:00"}
     * thisNo : {"actionNo":50641170,"actionTime":"11:24:00"}
     * diffTime : 281
     * validTime : 11:24:00
     * kjdTime : 30
     */

    private NoBean lastNo;
    private NoBean thisNo;
    private int diffTime;
    private String validTime;
    private int kjdTime;


    @Data
    public static class NoBean {
        /**
         * actionNo : 50641170
         * actionTime : 11:24:00
         */
        private int actionNo;
        private String actionTime;
    }

    // 50641200 13:54:00
    // 50641201 13:59:00
    public static void main(String[] args) {

    }

}
