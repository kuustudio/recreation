package com.royal.recreation.core;

import java.time.LocalDateTime;
import java.util.List;

public interface IGame {

    /**
     * 是否在开奖时间内
     */
    boolean inAction(LocalDateTime dateTime);

    long getNextNo(long actionNo);

    List<ActionInfo> actionInfoList(long currentNo, LocalDateTime currentEndTime, LocalDateTime maxEndTime);

}
