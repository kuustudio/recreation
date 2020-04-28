package com.royal.recreation.core.entity.inner;

import com.royal.recreation.core.type.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class AwardDetail {
    private UserType userType;
    private String awardUserId;
    private BigDecimal awardRate;
    private BigDecimal awardMoney;

    public static AwardDetail copy(AwardDetail awardDetail) {
        AwardDetail newAwardDetail = new AwardDetail();
        BeanUtils.copyProperties(awardDetail, newAwardDetail);
        return newAwardDetail;
    }

}