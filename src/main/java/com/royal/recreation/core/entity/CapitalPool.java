package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.PointRecordType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * 系统资金池
 */
@Data
@Document(collection = "capital_pool")
public class CapitalPool extends Domain {

    private String systemName = "游戏俱乐部";
    private Integer systemMaxBetPoint = 1000;
    private String systemNotice = "";
    // 余额
    private BigDecimal currentValue = BigDecimal.ZERO;
    // 充值总额
    private BigDecimal rechargeValue = BigDecimal.ZERO;
    // 撤资总额
    private BigDecimal cashOutValue = BigDecimal.ZERO;
    // 限制
    public Map<BonusLimitType, BigDecimal> systemLimit;
    // 奖金池变换累计
    private Map<PointRecordType, BigDecimal> detailMap = new HashMap<>();

}