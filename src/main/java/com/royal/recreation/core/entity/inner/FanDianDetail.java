package com.royal.recreation.core.entity.inner;

import com.royal.recreation.core.type.UserType;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.math.BigDecimal;

@Data
public class FanDianDetail {
    private UserType userType;
    private String fanDianUserId;
    private BigDecimal fanDianRate;
    private BigDecimal fanDianMoney;

    public static FanDianDetail copy(FanDianDetail fanDianDetail) {
        FanDianDetail newFanDianDetail1 = new FanDianDetail();
        BeanUtils.copyProperties(fanDianDetail, newFanDianDetail1);
        return newFanDianDetail1;
    }

}