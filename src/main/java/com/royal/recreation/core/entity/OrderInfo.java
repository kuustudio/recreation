package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.entity.inner.AwardDetail;
import com.royal.recreation.core.entity.inner.FanDianDetail;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.PlayedType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

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
    private List<FanDianDetail> fanDianDetails = Collections.emptyList();
    private List<AwardDetail> awardDetails = Collections.emptyList();

    // 中奖金额
    private BigDecimal bonusPropMoney;
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

    public BigDecimal totalFanDianMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (FanDianDetail fanDianDetail : fanDianDetails) {
            result = result.add(fanDianDetail.getFanDianMoney());
        }
        return result;
    }

    public BigDecimal totalAward() {
        BigDecimal result = BigDecimal.ZERO;
        for (AwardDetail awardDetail : awardDetails) {
            result = result.add(awardDetail.getAwardMoney());
        }
        return result;
    }

}
