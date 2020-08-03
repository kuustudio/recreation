package com.royal.recreation.util;

import com.royal.recreation.core.ActionInfo;
import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.entity.CapitalPool;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.type.GameType;
import com.royal.recreation.spring.mongo.Mongo;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Util {

    private static final Map<String, Lock> USER_POINT_BALANCE_LOCK = new ConcurrentHashMap<>();

    private static void lock(String userId) {
        USER_POINT_BALANCE_LOCK.computeIfAbsent(userId, k -> new ReentrantLock()).lock();
    }
    private static void unlock(String userId) {
        USER_POINT_BALANCE_LOCK.computeIfPresent(userId, (key, oldValue) -> {
            oldValue.unlock();
            return null;
        });
    }

    public static void insertUserPointList(List<UserPointRecord> userPointRecordList) {
        userPointRecordList.forEach(Util::insertUserPoint);
    }

    public static void insertUserPoint(UserPointRecord userPointRecord) {
        try {
            lock(userPointRecord.getUserId());
            UserInfo userInfo = Mongo.buildMongo().id(userPointRecord.getUserId(), UserInfo.class);
            userPointRecord.setBalance(userPointRecord.getValue().add(userInfo.getPoint()));
            Mongo.buildMongo().insert(userPointRecord);
            Mongo.buildMongo().id(userPointRecord.getUserId()).updateFirst(update -> update.inc("point", userPointRecord.getValue()), UserInfo.class);
            Mongo.buildMongo().updateFirst(update -> {
                BigDecimal negate = userPointRecord.getValue().negate();
                update.inc("currentValue", negate);
                update.inc("detailMap." + userPointRecord.getPointRecordType(), negate);
            }, CapitalPool.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            unlock(userPointRecord.getUserId());
        }

    }


    public static AwardInfo getLastAward(Integer typeId) {
        final LocalDateTime now = LocalDateTime.now();
        AwardInfo lastAward = Mongo.buildMongo().eq("typeId", typeId).desc("actionNo").findOne(AwardInfo.class);
        while (Duration.between(lastAward.getEndTime(), now).getSeconds() > 60 * GameType.find(typeId).getDuration()) {
            try {
                TimeUnit.MILLISECONDS.sleep(500);
            } catch (InterruptedException e) {
            }
            lastAward = Mongo.buildMongo().eq("typeId", typeId).desc("actionNo").findOne(AwardInfo.class);
            LocalDateTime actionNow = LocalDateTime.now();
            if (Duration.between(now, actionNow).getSeconds() > 3) {
                break;
            }
            if (!GameType.find(typeId).inAction(actionNow)) {
                break;
            }
        }
        return lastAward;
    }

    public static AwardInfo getLastAwardNoCheck(Integer typeId) {
        return Mongo.buildMongo().eq("typeId", typeId).desc("actionNo").findOne(AwardInfo.class);
    }

    public static List<ActionInfo> actionInfoList(Integer typeId) {
        AwardInfo lastAward = getLastAwardNoCheck(typeId);
        return GameType.find(typeId).actionInfoList(lastAward.getActionNo(), lastAward.getEndTime(), LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.MIN));
    }

    public static boolean greaterZero(BigDecimal value) {
        return value.compareTo(BigDecimal.ZERO) > 0;
    }


}
