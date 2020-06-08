package com.royal.recreation.controller;

import com.alibaba.fastjson.JSONObject;
import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.controller.base.BaseController;
import com.royal.recreation.core.entity.*;
import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.entity.inner.AwardDetail;
import com.royal.recreation.core.entity.inner.FanDianDetail;
import com.royal.recreation.core.type.*;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import com.royal.recreation.util.DateUtil;
import com.royal.recreation.util.SystemSetting;
import com.royal.recreation.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/game")
@Slf4j
public class GameController extends BaseController {

    @RequestMapping("/checkBuy")
    @ResponseBody
    public String checkBuy() {
        return "";
    }

    @RequestMapping("/getNo/{typeId}")
    @ResponseBody
    public Object getNo(@PathVariable int typeId) {
        AwardInfo lastAward = Mongo.buildMongo().eq("typeId", typeId).desc("actionNo").findOne(AwardInfo.class);
        long actionNo = GameType.find(lastAward.getTypeId()).getNextNo(lastAward.getActionNo());
        LocalDateTime actionTime = lastAward.getEndTime().plusMinutes(GameType.find(typeId).getDuration());
        return new HashMap<String, Object>() {{
            put("actionNo", actionNo);
            put("actionTime", actionTime.toLocalTime());
        }};
    }

    @RequestMapping("/getOrdered/{typeId}")
    public String getOrdered(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable int typeId, Model model) {
        LocalDateTime now = LocalDateTime.now();
        List<OrderInfo> orderInfoList = Mongo.buildMongo().eq("userId", userDetails.getId()).eq("time", DateUtil.dateTimeToTime(now))
//                .eq("typeId", typeId)
                .desc("_id").eq("status", Status.ACTIVE).find(OrderInfo.class);
        model.addAttribute("orderInfoList", orderInfoList);
        return "index/orderInfo";
    }

    /**
     * 撤单
     */
    @RequestMapping("/cancelBet")
    @Transactional
    public void cancelBet(@AuthenticationPrincipal MyUserDetails userDetails, String id, HttpServletResponse response) throws UnsupportedEncodingException {
        OrderInfo orderInfo = Mongo.buildMongo().id(id).eq("userId", userDetails.getId()).findOne(OrderInfo.class);
        LocalDateTime now = LocalDateTime.now();
        if (orderInfo == null || orderInfo.getState() != 3) {
            responseError("已经开奖,无法撤单");
            return;
        }
        if (Duration.between(now, orderInfo.getActionTime()).getSeconds() < Constant.kjdTime) {
            responseError("已经封盘,无法撤单");
            return;
        }
        Mongo.buildMongo().id(id).updateFirst(update -> {
            update.set("status", Status.DEL);
            update.set("updateAt", now);
            update.set("state", 5);
        }, OrderInfo.class);
        Mongo.buildMongo().eq("userId", userDetails.getId()).eq("businessId", orderInfo.getId()).eq("pointRecordType", PointRecordType.BET).updateFirst(update -> {
            update.set("status", Status.DEL);
        }, UserPointRecord.class);
        UserPointRecord cancelBetRecord = new UserPointRecord();
        cancelBetRecord.setUserId(userDetails.getId());
        cancelBetRecord.setPointRecordType(PointRecordType.CANCEL_BET);
        cancelBetRecord.setValue(BigDecimal.valueOf(orderInfo.getUsePoint()));
        cancelBetRecord.setRemark(String.format("单号:%s", orderInfo.getOrderNo()));
        cancelBetRecord.setBusinessId(orderInfo.getId());
        Util.insertUserPoint(cancelBetRecord);
    }

    private static final Map<String, Long> POST_CODE_LOCK = new ConcurrentHashMap<>();

    @RequestMapping("/postCode")
    @ResponseBody
    public Object postCode(@AuthenticationPrincipal MyUserDetails userDetails, PostCodeQuery query, HttpServletResponse response) {
        try {
            POST_CODE_LOCK.compute(userDetails.getId(), (key, oldValue) -> {
                long currentTimeMillis = System.currentTimeMillis();
                if (oldValue == null) {
                    return currentTimeMillis;
                }
                if (currentTimeMillis - oldValue < 1000) {
                    throw new RuntimeException("请勿重复提交");
                }
                return currentTimeMillis;
            });
        } catch (Exception e) {
            return responseError(e.getMessage());
        }

        Integer typeId = query.getPara().getInteger("type");
        GameType gameType = GameType.find(typeId);
        AwardInfo lastAward = Util.getLastAward(typeId);
        LocalDateTime now = LocalDateTime.now();
        Long actionNo = query.getPara().getLong("actionNo");
        if (actionNo - lastAward.getActionNo() != 1 && actionNo - lastAward.getActionNo() != 942 && actionNo - lastAward.getActionNo() != 53) {
            log.warn("请求期号不正确:{}-{}", lastAward.getActionNo(), actionNo);
            return responseError("请求期号不正确");
        }
        LocalDateTime kjTime = lastAward.getEndTime().plusMinutes(gameType.getDuration());
        if (Duration.between(now, kjTime).getSeconds() < Constant.kjdTime) {
            return responseError("已封盘");
        }
        if (!gameType.inAction(kjTime)) {
            return responseError("该彩种尚未开始");
        }
        List<JSONObject> codeList = query.getCode();
        if (codeList.isEmpty()) {
            return responseError("请投注");
        }
        String userId = userDetails.getId();
        UserInfo userInfo;
        List<FanDianDetail> fanDianDetails = new ArrayList<>();
        List<AwardDetail> awardDetails = new ArrayList<>();
        while (userId != null && (userInfo = Mongo.buildMongo().id(userId, UserInfo.class)) != null && userInfo.getUserType() != UserType.ADMIN) {
            BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
            FanDianDetail fanDianDetail = new FanDianDetail();
            AwardDetail awardDetail = new AwardDetail();
            fanDianDetails.add(fanDianDetail);
            awardDetails.add(awardDetail);
            fanDianDetail.setUserType(userInfo.getUserType());
            fanDianDetail.setFanDianUserId(userInfo.getId());
            fanDianDetail.setFanDianRate(bonusSetting.getFanDianRate());
            awardDetail.setUserType(userInfo.getUserType());
            awardDetail.setAwardUserId(userInfo.getId());
            awardDetail.setAwardRate(bonusSetting.getAwardRate());
            userId = userInfo.getPId();
        }
        UserInfo thisUserInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        BonusSetting thisBonusSetting = Mongo.buildMongo().id(thisUserInfo.getBonusSettingId(), BonusSetting.class);
        List<OrderInfo> orderInfoList = codeList.stream().map(code -> {
            Integer mode = code.getInteger("mode");
            Integer beiShu = code.getInteger("beiShu");
            String actionData = code.getString("actionData");
            Integer playedGroup = code.getInteger("playedGroup");
            Integer playedId = code.getInteger("playedId");
            PlayedType playedType = PlayedType.find(playedId);
            Integer actionNum = playedType.getActionNum(actionData);
            BigDecimal bonusPropRate = playedType.getBonusPropRate(actionData, thisBonusSetting);
            String playedName = playedType.getPlayedName(actionData);
            int usePointInt = actionNum * beiShu;
            BigDecimal usePoint = BigDecimal.valueOf(usePointInt);
            List<FanDianDetail> thisFanDianDetails = new ArrayList<>();
            BigDecimal lastFanDianRate = BigDecimal.ZERO;
            for (FanDianDetail fanDianDetail : fanDianDetails) {
                FanDianDetail newFaDianDetail = FanDianDetail.copy(fanDianDetail);
                thisFanDianDetails.add(newFaDianDetail);
                if (newFaDianDetail.getUserType() == UserType.MEMBER) {
                    // 会员没有返点
                    newFaDianDetail.setFanDianMoney(BigDecimal.ZERO);
                } else {
                    newFaDianDetail.setFanDianMoney(usePoint.multiply(newFaDianDetail.getFanDianRate().subtract(lastFanDianRate)).multiply(Constant.VALUE_PERCENT));
                }
                lastFanDianRate = newFaDianDetail.getFanDianRate();
            }
            List<AwardDetail> thisAwardDetails = new ArrayList<>();
            BigDecimal lastAwardRate = BigDecimal.ZERO;
            for (AwardDetail awardDetail : awardDetails) {
                AwardDetail newAwardDetail = AwardDetail.copy(awardDetail);
                thisAwardDetails.add(newAwardDetail);
                newAwardDetail.setAwardMoney(usePoint.multiply(newAwardDetail.getAwardRate().subtract(lastAwardRate)).multiply(Constant.VALUE_PERCENT));
                lastAwardRate = newAwardDetail.getAwardRate();
            }

            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setFanDianDetails(thisFanDianDetails);
            orderInfo.setAwardDetails(thisAwardDetails);
            orderInfo.setOrderNo(SystemSetting.getOrderNo());
            orderInfo.setUserId(userDetails.getId());
            orderInfo.setUsername(userDetails.getUsername());
            orderInfo.setWin(null);
            orderInfo.setTypeId(typeId);
            orderInfo.setTypeName(gameType.getName());
            orderInfo.setActionNo(actionNo);
            orderInfo.setActionTime(kjTime);

            orderInfo.setPlayedType(playedType);
            orderInfo.setUsePoint(usePointInt);

            orderInfo.setBonusPropMoney(bonusPropRate.multiply(BigDecimal.valueOf(beiShu)));
            orderInfo.setBonusPropRate(bonusPropRate);

            orderInfo.setMode(mode);
            orderInfo.setBeiShu(beiShu);
            orderInfo.setActionNum(actionNum);
            orderInfo.setActionData(actionData);
            orderInfo.setPlayedGroup(playedGroup);
            orderInfo.setPlayedId(playedId);
            orderInfo.setPlayedName(playedName);
            orderInfo.setValidateCode(UUID.randomUUID().toString().replace("-", ""));
            orderInfo.setState(3);
            orderInfo.setBonusLimitType(playedType.getBonusLimitType(actionData));

            orderInfo.setTime(kjTime.getYear() * 10000 + kjTime.getMonthValue() * 100 + kjTime.getDayOfMonth());
            return orderInfo;
        }).collect(Collectors.toList());
        int sum = orderInfoList.stream().mapToInt(OrderInfo::getUsePoint).sum();
        if (thisUserInfo.getPoint().intValue() < sum) {
            return responseError("积分不足,请联系管理员上分");
        }
        Mongo.buildMongo().insertAll(orderInfoList);
        List<UserPointRecord> userPointRecordList = orderInfoList.stream().map(orderInfo -> {
            UserPointRecord userPointRecord = new UserPointRecord();
            userPointRecord.setUserId(orderInfo.getUserId());
            userPointRecord.setPointRecordType(PointRecordType.BET);
            userPointRecord.setValue(BigDecimal.valueOf(orderInfo.getUsePoint()).negate());
            userPointRecord.setRemark(String.format("单号:%s", orderInfo.getOrderNo()));
            userPointRecord.setBusinessId(orderInfo.getId());
            return userPointRecord;
        }).collect(Collectors.toList());
        Util.insertUserPointList(userPointRecordList);
        if (thisUserInfo.getPrint()) {
            return Collections.singletonMap("ids", orderInfoList.stream().map(Domain::getId).collect(Collectors.joining(",")));
        } else {
            return "";
        }

    }

}
