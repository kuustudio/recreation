package com.royal.recreation.core;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@AllArgsConstructor
public class ActionInfo {

    // 期号
    private long actionNo;
    // 结束时间
    private LocalDateTime endTime;

    public String endTimeShow() {
        return endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

}
