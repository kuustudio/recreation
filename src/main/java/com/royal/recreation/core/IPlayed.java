package com.royal.recreation.core;

import com.royal.recreation.core.entity.BonusSetting;
import com.royal.recreation.core.type.BonusLimitType;

import java.math.BigDecimal;

public interface IPlayed {

    /**
     * 获得注数
     *
     * @param actionData 猜的什么
     * @return 注数
     */
    int getActionNum(String actionData);

    /**
     * 获得奖励倍数
     *
     * @param actionData   猜的什么
     * @param bonusSetting 用户奖励设置
     * @return 用户奖励倍数
     */
    BigDecimal getBonusPropRate(String actionData, BonusSetting bonusSetting);

    /**
     * 获得玩法名称
     *
     * @param actionData 猜的什么
     * @return 用户奖励倍数
     */
    String getPlayedName(String actionData);

    /**
     * 返回中奖的注数
     *
     * @param code       中奖数字
     * @param actionData 猜的什么
     * @return 中奖数量
     */
    int hit(String code, String actionData);

    BonusLimitType getBonusLimitType(String actionData);


}
