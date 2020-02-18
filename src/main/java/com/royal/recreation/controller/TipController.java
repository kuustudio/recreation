package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.core.entity.AwardInfo;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

@Controller
@RequestMapping("/Tip")
public class TipController {

    @RequestMapping("/getCZTip")
    @ResponseBody
    public Object getCZTip(HttpServletResponse response) throws IOException {
        return "";
    }

    @RequestMapping("/getZNX")
    @ResponseBody
    public Object getZNX(HttpServletResponse response) throws IOException {
        return "";
    }

    @RequestMapping("/getTKTip")
    @ResponseBody
    public Object getTKTip(HttpServletResponse response) throws IOException {
        return "";
    }

    @RequestMapping("/getNotice")
    @ResponseBody
    public Object getNotice(@AuthenticationPrincipal MyUserDetails userDetails, HttpSession session) throws IOException {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        if (user.getStatus() == Status.DEL) {
            session.invalidate();
            return new HashMap<String, Object>() {{
                put("flag", true);
                put("message", "账号已被冻结,请联系管理员");
            }};
        }
        if (Constant.SYSTEM_NOTICE.equals(session.getAttribute("systemNotice"))) {
            return "";
        } else {
            session.setAttribute("systemNotice", Constant.SYSTEM_NOTICE);
            return new HashMap<String, Object>() {{
                put("flag", true);
                put("message", Constant.SYSTEM_NOTICE);
            }};
        }

    }

    @RequestMapping("/getYKTip/{typeId}/{actionNo}")
    @ResponseBody
    public Object getTKTip(@PathVariable Integer typeId, @PathVariable Long actionNo) throws IOException {
        AwardInfo one = Mongo.buildMongo().eq("actionNo", actionNo).eq("typeId", typeId).findOne(AwardInfo.class);
        if (one != null) {
            return Collections.singletonMap("message", String.format("第%d期中奖号码是:%s", actionNo, one.getCode()));
        } else {
            return "";
        }
    }
}