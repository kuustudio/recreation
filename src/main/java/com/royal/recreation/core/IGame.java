package com.royal.recreation.core;

import java.time.LocalDateTime;

public interface IGame {

    /**
     * 是否在开奖时间内
     */
    boolean inAction(LocalDateTime dateTime);

}
