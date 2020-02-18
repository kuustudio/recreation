package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.UserType;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

@Data
@Document(collection = "bonus_setting")
public class BonusSetting extends Domain {

    // 规则名称
    private String settingName;
    // 官方玩法-大小单双
    private BigDecimal a1;
    // 官方玩法-猜数字
    private BigDecimal a2;
    // 官方玩法-总和龙虎和-猜和
    private BigDecimal a3;
    // 官方玩法-猜豹子
    private BigDecimal a4;
    // 官方玩法-猜顺子
    private BigDecimal a5;
    // 官方玩法-猜杂六,,半顺
    private BigDecimal a6;
    // 官方玩法-总和龙虎和-猜龙虎
    private BigDecimal a7;
//    private BigDecimal a8;
    // 官方玩法-猜对子
    private BigDecimal a9;
    // 对公玩法-三码不定位
    private BigDecimal a10;
    // 对公玩法-二码不定位
    private BigDecimal a11;
    // 对公玩法-二码定位
    private BigDecimal a12;
    // 对公玩法-三码定位
    private BigDecimal a13;
    // 对公玩法-四码定位
    private BigDecimal a14;
    // 对公玩法-一帆风顺
    private BigDecimal a15;
    // 对公玩法-好事成双
    private BigDecimal a16;
    // 对公玩法-三星报喜
    private BigDecimal a17;

    private BigDecimal fanDianRate = BigDecimal.ZERO;
    private BigDecimal awardRate = BigDecimal.ZERO;
}
