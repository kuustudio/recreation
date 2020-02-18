package com.royal.recreation.task;

import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

/**
 * 开奖service
 */
@Component
public class KjService {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final KjServiceAction kjServiceAction;

    public KjService(KjServiceAction kjServiceAction) {
        this.kjServiceAction = kjServiceAction;
    }

    public synchronized void action(Integer typeId) {
        List<AwardInfo> awardInfos = Mongo.buildMongo().eq("action", false).eq("typeId", typeId).find(AwardInfo.class);
        awardInfos.forEach(awardInfo -> {
            // 统计开奖数据
            Map<String, Object> statistics = new ConcurrentHashMap<>();
            // 成功开奖订单数
            statistics.put("successNum", new LongAdder());
            // 使用
            statistics.put("usePoint", new LongAdder());
            // 返点
            statistics.put("fanDianMoney", BigDecimal.ZERO);
            // 佣金
            statistics.put("awardMoney", BigDecimal.ZERO);
            // 奖金
            statistics.put("bonusPropMoney", BigDecimal.ZERO);

            List<OrderInfo> orderInfoList = Mongo.buildMongo().eq("actionNo", awardInfo.getActionNo()).eq("typeId", typeId).eq("status", Status.ACTIVE).eq("state", 3).find(OrderInfo.class);
            for (List<OrderInfo> value : orderInfoList.stream().collect(Collectors.groupingBy(OrderInfo::getUserId)).values()) {
                // 每期每用户
                // 奖金限制
                Map<BonusLimitType, BigDecimal> limitMap = new HashMap<>();
                value.forEach(orderInfo -> {
                    try {
                        boolean hit = kjServiceAction.actionOne(awardInfo, orderInfo, limitMap);
                        statistics.computeIfPresent("successNum", (key, oldValue) -> {
                            ((LongAdder) oldValue).increment();
                            return oldValue;
                        });
                        statistics.computeIfPresent("usePoint", (key, oldValue) -> {
                            ((LongAdder) oldValue).add(orderInfo.getUsePoint());
                            return oldValue;
                        });
                        statistics.computeIfPresent("fanDianMoney", (key, oldValue) -> ((BigDecimal) oldValue).add(orderInfo.getFanDianMoney()));
                        statistics.computeIfPresent("awardMoney", (key, oldValue) -> ((BigDecimal) oldValue).add(orderInfo.getAwardMoney()));
                        if (hit) {
                            statistics.computeIfPresent("bonusPropMoney", (key, oldValue) -> ((BigDecimal) oldValue).add(orderInfo.getBonusPropMoney()));
                        }
                    } catch (Exception e) {
                        logger.info("期号:[{}],订单:[{}],开奖失败", orderInfo.getActionNo(), orderInfo.getId(), e);
                    }
                });
            }
            int size = orderInfoList.size();
            long success = ((LongAdder) statistics.get("successNum")).sum();
            long usePoint = ((LongAdder) statistics.get("usePoint")).sum();
            BigDecimal fanDianMoney = (BigDecimal) statistics.get("fanDianMoney");
            BigDecimal awardMoney = (BigDecimal) statistics.get("awardMoney");
            BigDecimal bonusPropMoney = (BigDecimal) statistics.get("bonusPropMoney");
            BigDecimal profit = BigDecimal.valueOf(usePoint).subtract(fanDianMoney).subtract(awardMoney).subtract(bonusPropMoney);
            Mongo.buildMongo().eq("_id", new ObjectId(awardInfo.getId())).updateFirst(update -> {
                update.set("action", size == success);
                update.inc("usePoint", usePoint);
                update.inc("fanDianMoney", fanDianMoney);
                update.inc("awardMoney", awardMoney);
                update.inc("bonusPropMoney", bonusPropMoney);
                update.inc("profit", profit);
            }, AwardInfo.class);
            logger.info("期号[{}]开奖结果:未开奖订单数:[{}],执行成功:[{}]", awardInfo.getActionNo(), size, success);
        });

    }

}


