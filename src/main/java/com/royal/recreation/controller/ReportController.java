package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.controller.base.BaseController;
import com.royal.recreation.controller.base.BaseQuery;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.core.type.UserType;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import com.royal.recreation.util.DateUtil;
import org.bson.types.Decimal128;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/report")
public class ReportController extends BaseController {

    @RequestMapping("/search")
    public String search() {
        return "report/search";
    }

    @RequestMapping("/teamReport")
    public String teamReport(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        teamReportBiao(userDetails, model, baseQuery);
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        model.addAttribute("user", user);
        return "report/teamReport";
    }

    @RequestMapping("/teamReportBiao")
    public String teamReportBiao(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        List<UserInfo> queryUserList;
        if (userDetails.getUserType() == UserType.MEMBER) {
            queryUserList = Collections.singletonList(userInfo);
        } else {
            queryUserList = Mongo.buildMongo().eq("pId", userDetails.getId()).find(UserInfo.class);
            queryUserList.add(userInfo);
        }
        Map<String, Object> countMap = new HashMap<>();
        List<Map<String, Object>> result = queryUserList.stream().map(user -> {
            Mongo query = Mongo.buildMongo();
            queryOfDate(query, baseQuery);
            List<UserPointRecord> list = query
                    .eq("userId", user.getId())
                    .or("pointRecordType", PointRecordType.RECHARGE, PointRecordType.CASH_OUT, PointRecordType.BET, PointRecordType.FAN_DIAN, PointRecordType.BONUS, PointRecordType.AWARD)
                    .eq("status", Status.ACTIVE).find(UserPointRecord.class);

            Map<String, Object> one = new HashMap<>();
            BigDecimal profit = BigDecimal.ZERO;
            for (UserPointRecord userPointRecord : list) {
                PointRecordType pointRecordType = userPointRecord.getPointRecordType();
                one.merge(pointRecordType.toString(), userPointRecord.getValue(), (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
                countMap.merge(pointRecordType.toString(), userPointRecord.getValue(), (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
                if (userPointRecord.getPointRecordType() != PointRecordType.RECHARGE && userPointRecord.getPointRecordType() != PointRecordType.CASH_OUT) {
                    profit = profit.add(userPointRecord.getValue());
                }
            }
            one.put("profit", profit);
            countMap.merge("profit", profit, (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
            one.put("username", user.getUsername());
            return one;
        }).collect(Collectors.toList());
        countMap.put("username", "合计");
        result.add(countMap);
        model.addAttribute("list", result);
        return "report/teamReportBiao";
    }

    @RequestMapping("/counts")
    public String counts(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        countSearchs(userDetails, 1, model, baseQuery);
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        model.addAttribute("user", user);
        return "report/counts";
    }

    @RequestMapping("/countSearchs")
    public String countSearchs(@AuthenticationPrincipal MyUserDetails userDetails, int interval, Model model, BaseQuery baseQuery) {
        Mongo query = getBaseMongoQuery(userDetails, baseQuery, model);

        LocalDateTime now = LocalDateTime.now();
        int time = DateUtil.dateTimeToTime(now);
        int start = DateUtil.dateTimeToTime(now.minusDays(interval));
        List<UserPointRecord> userPointRecordList = query
                .between("time", start, time, Mongo.Between.EQ)
                .or("pointRecordType", PointRecordType.BET, PointRecordType.FAN_DIAN, PointRecordType.BONUS, PointRecordType.AWARD)
                .eq("status", Status.ACTIVE).find(UserPointRecord.class);
        Map<String, Object> countMap = new HashMap<>();
        countMap.put("date", "合计");
        List<Map<String, Object>> result = userPointRecordList.stream().sorted(Comparator.comparingInt(Domain::getTime)).collect(Collectors.groupingBy(Domain::getTime, LinkedHashMap::new, Collectors.toList()))
                .values().stream().map(list -> {
                    Map<String, Object> one = new HashMap<>();
                    BigDecimal profit = BigDecimal.ZERO;
                    for (UserPointRecord userPointRecord : list) {
                        PointRecordType pointRecordType = userPointRecord.getPointRecordType();
                        one.merge(pointRecordType.toString(), userPointRecord.getValue(), (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
                        countMap.merge(pointRecordType.toString(), userPointRecord.getValue(), (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
                        profit = profit.add(userPointRecord.getValue());
                    }
                    one.put("date", list.get(0).getCreateAt().toLocalDate().toString());
                    one.put("profit", profit);
                    countMap.merge("profit", profit, (o, n) -> ((BigDecimal) o).add((BigDecimal) n));
                    return one;
                }).collect(Collectors.toList());
        model.addAttribute("list", result);
        model.addAttribute("countMap", countMap);
        return "report/countsBiao";
    }

    @RequestMapping("/coin")
    public String coin(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        model.addAttribute("user", user);
        coinlog(userDetails, model, baseQuery, null);
        return "report/coin";
    }

    @RequestMapping("/coinlog/0")
    public String coinlog(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery, PointRecordType liqType) {
        Mongo query = Mongo.buildMongo();
        queryOfUser(query, userDetails, baseQuery, model);
        queryOfTime(query, baseQuery);
        if (liqType != null) {
            query.eq("pointRecordType", liqType);
        }
        int count = (int) query.count(UserPointRecord.class);
        List<UserPointRecord> list = query.desc("_id").limit(Constant.limit, baseQuery.getPage()).find(UserPointRecord.class);
        model.addAttribute("list", list);
        model.addAttribute("page", baseQuery.getPage());
        model.addAttribute("pages", count % Constant.limit == 0 ? count / Constant.limit : (count / Constant.limit + 1));
        model.addAttribute("queryString", getQueryString("/report/coinlog/0"));
        return "report/coinBiao";
    }

    @RequestMapping("/count")
    public String count(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        model.addAttribute("user", user);
        countSearch(userDetails, model, baseQuery);
        return "report/count";
    }

    @RequestMapping("countSearch")
    public String countSearch(@AuthenticationPrincipal MyUserDetails userDetails, Model model, BaseQuery baseQuery) {
        Mongo rechargeQuery = getBaseMongoQuery(userDetails, baseQuery, model);
        rechargeQuery.eq("status", Status.ACTIVE.toString());
        List<Mongo.GroupResult> resultList = rechargeQuery.sumGroup("value", UserPointRecord.class, "pointRecordType");
        Map<PointRecordType, BigDecimal> result = resultList.stream().collect(Collectors.toMap(g -> PointRecordType.valueOf(g.getId()), g -> ((Decimal128) g.getValue()).bigDecimalValue()));
        BigDecimal recharge = result.getOrDefault(PointRecordType.RECHARGE, BigDecimal.ZERO);
        BigDecimal cashOut = result.getOrDefault(PointRecordType.CASH_OUT, BigDecimal.ZERO);
        BigDecimal bet = result.getOrDefault(PointRecordType.BET, BigDecimal.ZERO);
        BigDecimal bonus = result.getOrDefault(PointRecordType.BONUS, BigDecimal.ZERO);
        BigDecimal fanDian = result.getOrDefault(PointRecordType.FAN_DIAN, BigDecimal.ZERO);
        BigDecimal award = result.getOrDefault(PointRecordType.AWARD, BigDecimal.ZERO);

        model.addAttribute("recharge", recharge);
        model.addAttribute("cashOut", cashOut);
        model.addAttribute("bet", bet);
        model.addAttribute("bonus", bonus);
        model.addAttribute("fanDian", fanDian);
        model.addAttribute("award", award);
        model.addAttribute("profit", bet.add(bonus).add(fanDian).add(award));

        return "report/countBiao";
    }

}
