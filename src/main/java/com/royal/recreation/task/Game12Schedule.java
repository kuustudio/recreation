package com.royal.recreation.task;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.type.GameType;
import com.royal.recreation.spring.SpringApplicationContext;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 抓取欧洲幸运5数据
 */
@Component
public class Game12Schedule implements InitializingBean {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final SpringApplicationContext context;
    private final KjService kjService;

    @Autowired
    public Game12Schedule(SpringApplicationContext context, KjService kjService) {
        this.context = context;
        this.kjService = kjService;
    }

    @Scheduled(cron = "0 * * * * ? ")
    public synchronized void action12() {
        LocalDateTime actionTime = LocalDateTime.now();
        while (!action12List(actionTime)) {
            if (Duration.between(actionTime, LocalDateTime.now()).getSeconds() > 55) {
                logger.info("一分钟内未抓取到数据,放弃");
                break;
            } else {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                }
                logger.info("未正确获取到信息,继续");
            }

        }
    }

    private boolean action12List(LocalDateTime actionTime) {
        if (!getGameType().inAction(actionTime)) {
            return true;
        }
        return action0(actionTime) || action1(actionTime) || action2(actionTime);
    }

    private boolean action0(LocalDateTime actionTime) {
        boolean successFlag = false;
        String url = "https://www.auluckylottery.com/results/lucky-ball-5";
        logger.info("开始爬取数据:{}", url);
        String result = null;
        try {
            result = HttpUtil.httpGet(url).replaceAll("[\n\t\r]", "");
            if (StringUtils.isEmpty(result)) {
                return false;
            }
            String patternStr = "<div class=\"past_numbers\" id=\"\"><div class=\"pn_font1\">(.{26}) \\(ACST\\)  &nbsp;&nbsp;Draw:  <strong>(.{8})</strong></div><div class=\"brt2_ball p_number_ball\"><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div></div></div>";
            Pattern pattern = Pattern.compile(patternStr);
            Matcher matcher = pattern.matcher(result);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy hh:mm a", Locale.ENGLISH);
            while (matcher.find()) {
                LocalDateTime endTime = LocalDateTime.parse(matcher.group(1).replace("am", "AM").replace("pm", "PM"), dateTimeFormatter).minusMinutes(90);
                Long actionNo = Long.valueOf(matcher.group(2));
                String code = String.format("%s,%s,%s,%s,%s", matcher.group(3), matcher.group(4), matcher.group(5), matcher.group(6), matcher.group(7));

                if (actionTime != null && Duration.between(endTime, actionTime).getSeconds() < 5 * 60 && !successFlag) {
                    successFlag = true;
                }
                AwardInfo oldAwardInfo = Mongo.buildMongo().eq("actionNo", actionNo).eq("typeId", 12).findOne(AwardInfo.class);
                if (oldAwardInfo == null && successFlag) {
                    oldAwardInfo = new AwardInfo();
                    oldAwardInfo.setTypeId(12);
                    oldAwardInfo.setActionNo(actionNo);

                    oldAwardInfo.setCode(code);
                    oldAwardInfo.setEndTime(endTime);
                    oldAwardInfo.setStartTime(endTime.minusMinutes(5));
                    oldAwardInfo.setAction(false);
                    Mongo.buildMongo().insert(oldAwardInfo);
                    logger.info("成功获得中奖信息{}=={}", oldAwardInfo.getActionNo(), oldAwardInfo.getCode());
                    kjService.action(12);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("数据:{}", result);
            logger.info("action12出现异常", e);
        } finally {
            logger.info("执行结果:{}", successFlag);
        }
        return successFlag;
    }

    private boolean action1(LocalDateTime actionTime) {
        boolean successFlag = false;
        String url = "https://88888kai.com/Open/CurrentOpen.aspx?code=10076&_=0.4794571300382";
        logger.info("开始爬取数据:{}", url);
        String result = null;
        try {
            result = HttpUtil.httpGet(url);
            if (StringUtils.isEmpty(result)) {
                return false;
            }
            JSONObject json = JSON.parseObject(result);
            JSONArray dataArr = json.getJSONArray("list");
            for (Object obj : dataArr) {
                JSONObject data = (JSONObject) obj;
                String cd = data.getString("c_d");
                LocalDateTime endTime = LocalDateTime.of(LocalDate.parse(LocalDateTime.now().getYear() + "/" + cd.substring(0, 5), DateTimeFormatter.ofPattern("yyyy/M/d")), LocalTime.parse(cd.substring(13), DateTimeFormatter.ofPattern("HH:mm:ss"))).plusSeconds(20);
                Long actionNo = data.getLong("c_t");
                String code = data.getString("c_r");

                if (actionTime != null && Duration.between(endTime, actionTime).getSeconds() < 5 * 60 && !successFlag) {
                    successFlag = true;
                }
                AwardInfo oldAwardInfo = Mongo.buildMongo().eq("actionNo", actionNo).eq("typeId", 12).findOne(AwardInfo.class);
                if (oldAwardInfo == null && successFlag) {
                    oldAwardInfo = new AwardInfo();
                    oldAwardInfo.setTypeId(12);
                    oldAwardInfo.setActionNo(actionNo);

                    oldAwardInfo.setCode(code);
                    oldAwardInfo.setEndTime(endTime);
                    oldAwardInfo.setStartTime(endTime.minusMinutes(5));
                    oldAwardInfo.setAction(false);
                    Mongo.buildMongo().insert(oldAwardInfo);
                    logger.info("成功获得中奖信息{}=={}", oldAwardInfo.getActionNo(), oldAwardInfo.getCode());
                    kjService.action(12);
                } else {
                    break;
                }
            }
        } catch (Exception e) {
            logger.info("数据:{}", result);
            logger.info("action12出现异常", e);
        } finally {
            logger.info("执行结果:{}", successFlag);
        }
        return successFlag;
    }

    private boolean action2(LocalDateTime actionTime) {
        boolean successFlag = false;
        List<String> urls = Arrays.asList("https://api.api861861.com/CQShiCai/getBaseCQShiCaiList.do?lotCode=10010",
                "https://www.369kj.com/CQShiCai/getBaseCQShiCaiList.do?lotCode=10010");
        for (String url : urls) {
            logger.info("开始爬取数据:{}", url);
            String result = null;
            try {
                result = HttpUtil.httpGet(url);
                if (StringUtils.isEmpty(result)) {
                    continue;
                }
                JSONObject json = JSON.parseObject(result);
                JSONArray dataArr = json.getJSONObject("result").getJSONArray("data");
                for (Object obj : dataArr) {
                    JSONObject data = (JSONObject) obj;
                    LocalDateTime endTime = LocalDateTime.parse(data.getString("preDrawTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusSeconds(20);
                    Long actionNo = data.getLong("preDrawIssue");
                    String code = data.getString("preDrawCode");

                    if (actionTime != null && Duration.between(endTime, actionTime).getSeconds() < 5 * 60 && !successFlag) {
                        successFlag = true;
                    }
                    AwardInfo oldAwardInfo = Mongo.buildMongo().eq("actionNo", actionNo).eq("typeId", 12).findOne(AwardInfo.class);
                    if (oldAwardInfo == null && successFlag) {
                        oldAwardInfo = new AwardInfo();
                        oldAwardInfo.setTypeId(12);
                        oldAwardInfo.setActionNo(actionNo);

                        oldAwardInfo.setCode(code);
                        oldAwardInfo.setEndTime(endTime);
                        oldAwardInfo.setStartTime(endTime.minusMinutes(5));
                        oldAwardInfo.setAction(false);
                        Mongo.buildMongo().insert(oldAwardInfo);
                        logger.info("成功获得中奖信息{}=={}", oldAwardInfo.getActionNo(), oldAwardInfo.getCode());
                        kjService.action(12);
                    } else {
                        break;
                    }
                }
            } catch (Exception e) {
                logger.info("数据:{}", result);
                logger.info("action12出现异常", e);
                continue;
            } finally {
                logger.info("执行结果:{}", successFlag);
            }
            if (successFlag) {
                return true;
            }
        }
        return false;
    }

    public GameType getGameType() {
        return GameType.Australia;
    }

    @Override
    public void afterPropertiesSet() {
        action12List(null);
    }


    public static void main(String[] args) {
        String str = HttpUtil.httpGet("https://www.auluckylottery.com/results/lucky-ball-5").replaceAll("[\n\t\r]", "");
        // <div class="past_numbers more_result" id=""><div class="pn_font1">Thu, Jul 30, 2020 08:19 pm (ACST)  &nbsp;&nbsp;Draw:  <strong>50706347</strong></div><div class="brt2_ball p_number_ball"><div class="back_red">8</div><div class="back_red">4</div><div class="back_red">4</div><div class="back_red">4</div><div class="back_red">0</div></div></div>
        String patternStr =
                "<div class=\"past_numbers\" id=\"\"><div class=\"pn_font1\">(.{26}) \\(ACST\\)  &nbsp;&nbsp;Draw:  <strong>(.{8})</strong></div><div class=\"brt2_ball p_number_ball\"><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div><div class=\"back_red\">(.)</div></div></div>";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(str);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy hh:mm a", Locale.ENGLISH);
        while (matcher.find()) {
            System.out.println(LocalDateTime.parse(matcher.group(1).replace("am", "AM").replace("pm", "PM"), dateTimeFormatter).minusMinutes(90));
            System.out.println(matcher.group(2));
            System.out.println(matcher.group(3) + matcher.group(4) +matcher.group(5)+matcher.group(6)+matcher.group(7));
        }
    }
}
