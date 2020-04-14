package com.royal.recreation.task;

import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Util;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

import static com.royal.recreation.core.type.BonusLimitType.L_ALL;

/**
 * 一个订单,一个事务
 */
@Component
class KjServiceAction {

    @Transactional
    public boolean actionOne(AwardInfo awardInfo, OrderInfo orderInfo, Map<BonusLimitType, BigDecimal> limitMap) {
        // 返点
        if (Util.greaterZero(orderInfo.getFanDianMoney())) {
            UserPointRecord fanDianRecord = new UserPointRecord();
            fanDianRecord.setUserId(orderInfo.getFanDianUserId());
            fanDianRecord.setPointRecordType(PointRecordType.FAN_DIAN);
            fanDianRecord.setValue(orderInfo.getFanDianMoney());
            fanDianRecord.setRemark(String.format("单号:%s", orderInfo.getOrderNo()));
            fanDianRecord.setBusinessId(orderInfo.getId());
            Util.insertUserPoint(fanDianRecord);
        }
        int hit = orderInfo.getPlayedType().hit(awardInfo.getCode(), orderInfo.getActionData());
        if (hit > 0) {
            // 中奖
            BigDecimal originBonus = orderInfo.getBonusPropMoney().multiply(BigDecimal.valueOf(hit));
            BigDecimal bonusPropMoney = originBonus;
            if (orderInfo.getBonusLimitType() != null) {
                BigDecimal oneLimit = limitMap.computeIfAbsent(orderInfo.getBonusLimitType(), BonusLimitType::getRealLimit);
                bonusPropMoney = bonusPropMoney.compareTo(oneLimit) > 0 ? oneLimit : bonusPropMoney;
            }
            BigDecimal allLimit = limitMap.computeIfAbsent(L_ALL, BonusLimitType::getRealLimit);
            bonusPropMoney = bonusPropMoney.compareTo(allLimit) > 0 ? allLimit : bonusPropMoney;

            BigDecimal finalBonusPropMoney = bonusPropMoney;

            if (orderInfo.getBonusLimitType() != null) {
                limitMap.computeIfPresent(orderInfo.getBonusLimitType(), (key, oldValue) -> oldValue.subtract(finalBonusPropMoney));
            }
            limitMap.computeIfPresent(L_ALL, (key, oldValue) -> oldValue.subtract(finalBonusPropMoney));

            if (originBonus.compareTo(finalBonusPropMoney) != 0) {
                UserPointRecord bonusRecord = new UserPointRecord();
                bonusRecord.setUserId(orderInfo.getUserId());
                bonusRecord.setPointRecordType(PointRecordType.BONUS);
                bonusRecord.setValue(finalBonusPropMoney);
                bonusRecord.setRemark(String.format("奖金超过限制,单号:%s", orderInfo.getOrderNo()));
                bonusRecord.setBusinessId(orderInfo.getId());
                Util.insertUserPoint(bonusRecord);
                Mongo.buildMongo().id(orderInfo.getId()).updateFirst(update -> {
                    update.set("win", true);
                    update.set("code", awardInfo.getCode());
                    update.set("updateAt", LocalDateTime.now());
                    update.set("state", 1);
                    update.set("bonusLimited", true);
                    update.set("bonusPropMoney", finalBonusPropMoney);
                }, OrderInfo.class);
            } else {
                UserPointRecord bonusRecord = new UserPointRecord();
                bonusRecord.setUserId(orderInfo.getUserId());
                bonusRecord.setPointRecordType(PointRecordType.BONUS);
                bonusRecord.setValue(finalBonusPropMoney);
                bonusRecord.setRemark(String.format("单号:%s", orderInfo.getOrderNo()));
                bonusRecord.setBusinessId(orderInfo.getId());
                Util.insertUserPoint(bonusRecord);
                Mongo.buildMongo().id(orderInfo.getId()).updateFirst(update -> {
                    update.set("win", true);
                    update.set("code", awardInfo.getCode());
                    update.set("updateAt", LocalDateTime.now());
                    update.set("state", 1);
                    update.set("bonusLimited", false);
                    update.set("bonusPropMoney", finalBonusPropMoney);
                }, OrderInfo.class);
            }

            orderInfo.setBonusPropMoney(finalBonusPropMoney);
            return true;
        } else {
            // 未中奖
            Mongo.buildMongo().id(orderInfo.getId()).updateFirst(update -> {
                update.set("win", false);
                update.set("code", awardInfo.getCode());
                update.set("updateAt", LocalDateTime.now());
                update.set("state", 2);
            }, OrderInfo.class);
            return false;
        }
    }

}