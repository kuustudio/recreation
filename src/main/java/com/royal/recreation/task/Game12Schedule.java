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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * 抓取欧洲幸运5数据
 */
@Component
@Profile({"test", "pro"})
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
        String url = "https://api.api861861.com/CQShiCai/getBaseCQShiCaiList.do?lotCode=10010";
        logger.info("开始爬取数据:{}", url);
        boolean successFlag = false;
        String result = HttpUtil.httpGet(url);
        try {
            if (StringUtils.isEmpty(result)) {
                return false;
            }
            JSONObject json = JSON.parseObject(result);
            JSONArray dataArr = json.getJSONObject("result").getJSONArray("data");
            for (Object obj : dataArr) {
                JSONObject data = (JSONObject) obj;
                LocalDateTime endTime = LocalDateTime.parse(data.getString("preDrawTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).plusSeconds(20);
                Long actionNo = data.getLong("preDrawIssue");
                String code = data.getString("preDrawCode");

                if (actionTime != null && Duration.between(endTime, actionTime).getSeconds() < 5 * 60) {
                    successFlag = true;
                }
                AwardInfo oldAwardInfo = Mongo.buildMongo().eq("actionNo", actionNo).eq("typeId", 12).findOne(AwardInfo.class);
                if (oldAwardInfo == null) {
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
            successFlag = false;
            logger.info("action12出现异常", e);
        } finally {
            logger.info("执行结果:{}", successFlag);
        }
        return successFlag;
    }

    public GameType getGameType() {
        return GameType.Australia;
    }

    @Override
    public void afterPropertiesSet() {
        action12List(null);
    }
}
