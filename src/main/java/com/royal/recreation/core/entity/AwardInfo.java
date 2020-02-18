package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 开奖信息
 */
@Data
//@Document(collection = "#{'award_info_'+@yearMonthData.data}")
@Document(collection = "award_info")
public class AwardInfo extends Domain {
    // 彩票游戏
    private Integer typeId;
    // 期号
    @Indexed
    private Long actionNo;
    // 中奖号
    private String code;

    // 开始时间
    private LocalDateTime startTime;
    // 结束时间
    private LocalDateTime endTime;
    // 是否已开奖
    @Indexed
    private Boolean action;

    private Long usePoint;
    private BigDecimal fanDianMoney;
    private BigDecimal awardMoney;
    private BigDecimal bonusPropMoney;
    private BigDecimal profit;

}
