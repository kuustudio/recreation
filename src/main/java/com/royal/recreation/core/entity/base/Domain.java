package com.royal.recreation.core.entity.base;

import com.royal.recreation.core.type.Status;
import com.royal.recreation.util.DateUtil;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDateTime;

import static com.royal.recreation.util.DateUtil.DATE_TIME_FORMATTER;
import static com.royal.recreation.util.DateUtil.TIME_FORMATTER;

@Data
public class Domain {

    @Id
    private String id;
    private LocalDateTime createAt = LocalDateTime.now();
    private LocalDateTime updateAt = LocalDateTime.now();
    // yyyyMMdd
    @Indexed
    private Integer time = DateUtil.dateTimeToTime(LocalDateTime.now());
    private Status status = Status.ACTIVE;

    public String createTimeShow() {
        return this.createAt.format(TIME_FORMATTER);
    }
    public String createDateTimeShow() {
        return this.createAt.format(DATE_TIME_FORMATTER);
    }

}