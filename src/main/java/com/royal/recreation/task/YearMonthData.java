package com.royal.recreation.task;

import lombok.Data;
import lombok.Getter;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component(value = "yearMonthData")
@Data
public class YearMonthData implements InitializingBean {

    private int year;
    private int month;

    // 每月一号执行一次
    @Scheduled(cron = "0 0 0 1 * ?")
    public void action() {
        // 怕有时差
        LocalDateTime now = LocalDateTime.now().plusDays(1);
        year = now.getYear();
        month = now.getMonthValue();
    }

    public String getData() {
        return String.format("%d_%d", year, month);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        year = now.getYear();
        month = now.getMonthValue();
    }
}
