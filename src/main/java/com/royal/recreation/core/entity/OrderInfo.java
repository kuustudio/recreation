package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.PlayedType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static com.royal.recreation.util.DateUtil.DATE_TIME_FORMATTER;

@Data
@Document(collection = "order_info")
public class OrderInfo extends Domain {

    @Indexed
    private String orderNo;
    @Indexed
    private String userId;
    private String username;
    //开奖号码
    private String code;
    // 是否中奖
    private Boolean win;
    private Integer typeId;
    private String typeName;
    @Indexed
    private Long actionNo;
    private LocalDateTime actionTime;
    private PlayedType playedType;
    // 使用积分
    private Integer usePoint;
    // 返点金额
    private BigDecimal fanDianMoney;
    private String fanDianUserId;
    // 佣金
    private BigDecimal awardMoney;
    // 中奖金额
    private BigDecimal bonusPropMoney;

    // 返点比例
    private BigDecimal fanDianRate;
    // 佣金比例
    private BigDecimal awardRate;
    // 中奖比例
    private BigDecimal bonusPropRate;

    private Integer mode;
    private Integer beiShu;
    private Integer actionNum;
    private String actionData;
    private Integer playedGroup;
    private Integer playedId;
    private String playedName;

    private String validateCode;
    private Integer state;
    private BonusLimitType bonusLimitType;
    private Boolean bonusLimited = Boolean.FALSE;


    public String actionTimeShow() {
        return this.actionTime.format(DATE_TIME_FORMATTER);
    }

}
