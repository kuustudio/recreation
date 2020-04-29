package com.royal.recreation.task;

import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.entity.inner.AwardDetail;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.DateUtil;
import com.royal.recreation.util.Util;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 每天凌晨发放佣金
 */
@Component
public class AwardSchedule {

    @Scheduled(
            cron = "0 20 0 * * ? "
//            fixedDelay = 1000 * 60 * 60
    )
    @Transactional
    public void action12() {
        LocalDateTime actionTime = LocalDateTime.now().minusDays(1);
        action(actionTime);
    }

    public void action(LocalDateTime actionTime) {
        int time = DateUtil.dateTimeToTime(actionTime);
        List<OrderInfo> orderInfoList = Mongo.buildMongo().eq("time", time).eq("status", Status.ACTIVE).find(OrderInfo.class);
        orderInfoList.stream().map(OrderInfo::getAwardDetails).flatMap(List::stream).collect(Collectors.groupingBy(AwardDetail::getAwardUserId))
                .values().forEach(awardDetails -> {
            BigDecimal result = BigDecimal.ZERO;
            for (AwardDetail awardDetail : awardDetails) {
                if (awardDetail.getAwardMoney().compareTo(BigDecimal.ZERO) > 0) {
                    result = result.add(awardDetail.getAwardMoney());
                }
            }
            if (result.compareTo(BigDecimal.ZERO) > 0) {
                UserPointRecord userPointRecord = new UserPointRecord();
                userPointRecord.setUserId(awardDetails.get(0).getAwardUserId());
                userPointRecord.setPointRecordType(PointRecordType.AWARD);
                userPointRecord.setValue(result);
                userPointRecord.setRemark(time + "所获佣金");
                userPointRecord.setTime(time);
                Util.insertUserPoint(userPointRecord);
            }
        });
    }

}
