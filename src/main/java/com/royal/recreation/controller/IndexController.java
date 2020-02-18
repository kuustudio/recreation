package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.entity.BonusSetting;
import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.GameType;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import com.royal.recreation.util.DateUtil;
import com.royal.recreation.util.Util;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class IndexController {

    @RequestMapping("/")
    public String index(@AuthenticationPrincipal MyUserDetails userDetails, Model model, Integer typeId) {
        LocalDateTime now = LocalDateTime.now();
        List<OrderInfo> orderInfoList = Mongo.buildMongo().eq("userId", userDetails.getId()).eq("time", DateUtil.dateTimeToTime(now)).eq("status", Status.ACTIVE).desc("_id").find(OrderInfo.class);
        UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
        typeId = typeId == null ? 12 : typeId;
        model.addAttribute("orderInfoList", orderInfoList);
        model.addAttribute("user", userInfo);
        model.addAttribute("bonusSetting", bonusSetting);
        model.addAttribute("typeId", typeId);
        model.addAttribute("typeName", GameType.find(typeId).getName());
        model.addAllAttributes(getQiHao(typeId));
        return "index/index";
    }

    @RequestMapping("/index/group/{typeId}/{id}")
    public String group(@PathVariable int typeId, @PathVariable int id, Model model) {
        model.addAttribute("typeId", typeId);
        model.addAttribute("id", id);
        return "index/group" + id;
    }

    @RequestMapping("/index/playTips/{id}")
    public String playTips(@PathVariable int id) {
        return "index/playTips" + id;
    }

    @RequestMapping("/index/played/{typeId}/{id}")
    public String played(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable int typeId, @PathVariable int id, Model model) {
        UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        BonusSetting bonusSetting = Mongo.buildMongo().id(userInfo.getBonusSettingId(), BonusSetting.class);
        model.addAttribute("user", userInfo);
        model.addAttribute("bonusSetting", bonusSetting);
        model.addAttribute("typeId", typeId);
        model.addAttribute("id", id);
        return "index/played" + id;
    }


    @RequestMapping("/index/getQiHao/{typeId}")
    @ResponseBody
    public Map<String, Object> getQiHao(@PathVariable int typeId) {
        return new HashMap<String, Object>() {{
            AwardInfo lastAward = Util.getLastAward(typeId);
            put("lastNo", new HashMap<String, Object>() {{
                put("actionNo", lastAward.getActionNo());
                put("actionTime", lastAward.getEndTime().toLocalTime());
                put("awardNo", lastAward.getCode());
            }});
            LocalDateTime now = LocalDateTime.now();
            long actionNo = lastAward.getActionNo() + 1;
            LocalDateTime actionTime = lastAward.getEndTime().plusMinutes(GameType.find(typeId).getDuration());
            put("thisNo", new HashMap<String, Object>() {{
                put("actionNo", actionNo);
                put("actionTime", actionTime.toLocalTime());
            }});
            put("diffTime", Duration.between(now, actionTime).getSeconds());
            put("validTime", actionTime.toLocalTime());
            put("kjdTime", Constant.kjdTime);
        }};
    }

    @RequestMapping("/index/getLastKjData/{typeId}")
    @ResponseBody
    public Object getLastKjData(@PathVariable int typeId,
                                HttpServletResponse response) {
        LocalDateTime now = LocalDateTime.now();
        GameType gameType = GameType.find(typeId);
        AwardInfo lastAward = Mongo.buildMongo().eq("typeId", typeId).desc("actionNo").findOne(AwardInfo.class);

        LocalDateTime actionTime = lastAward.getEndTime().plusMinutes(gameType.getDuration());

        if (Math.abs(Duration.between(now, actionTime).getSeconds()) < gameType.getKjTime()) {
            return "";
        }

        long actionNo = lastAward.getActionNo() + 1;
        long diffTime = Duration.between(now, actionTime).getSeconds();
        return new HashMap<String, Object>() {{
            put("actionNo", lastAward.getActionNo());
            put("actionTime", lastAward.getEndTime().toLocalTime());
            put("data", lastAward.getCode());
            put("actionName", "重庆时时彩");
            put("thisNo", actionNo);
            put("diffTime", diffTime);
            put("kjdTime", Constant.kjdTime);
        }};
    }

    @RequestMapping("/index/printis/{printFlg}")
    @ResponseBody
    public void getLastKjData(@AuthenticationPrincipal MyUserDetails userDetails, @PathVariable int printFlg) {
        Mongo.buildMongo().id(userDetails.getId()).updateFirst(update -> {
            update.set("print", printFlg == 1);
        }, UserInfo.class);
    }

}
