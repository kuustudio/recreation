package com.royal.recreation.task;

import com.royal.recreation.core.entity.BonusSetting;
import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import com.royal.recreation.util.DateUtil;
import com.royal.recreation.util.Util;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 每天凌晨发放佣金
 */
@Component
public class AwardSchedule {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MongoTemplate mongoTemplate;

    public AwardSchedule(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

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


        Criteria criteria = new Criteria();
        criteria.and("time").is(time).and("fanDianUserId").ne(null).and("status").is(Status.ACTIVE);
        GroupOperation groupOperation = new GroupOperation(Fields.fields("fanDianUserId"));
        AggregationResults<AwardDay> aggregate = mongoTemplate.aggregate(Aggregation.newAggregation(new MatchOperation(criteria), groupOperation.sum("usePoint").as("usePoint")), OrderInfo.class, AwardDay.class);
        List<AwardDay> mappedResults = aggregate.getMappedResults();
        mappedResults.forEach(awardDay -> {
            UserInfo userInfo = Mongo.buildMongo().id(awardDay.getId(), UserInfo.class);
            BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
            UserPointRecord userPointRecord = new UserPointRecord();
            userPointRecord.setUserId(awardDay.getId());
            userPointRecord.setPointRecordType(PointRecordType.AWARD);
            userPointRecord.setValue(BigDecimal.valueOf(awardDay.getUsePoint()).multiply(bonusSetting.getAwardRate()).multiply(Constant.VALUE_PERCENT));
            userPointRecord.setRemark(time + "所获佣金");
            userPointRecord.setTime(time);
            Util.insertUserPoint(userPointRecord);
        });
    }

    @Data
    public static class AwardDay {
        private String id;
        private Long usePoint;
    }

}
