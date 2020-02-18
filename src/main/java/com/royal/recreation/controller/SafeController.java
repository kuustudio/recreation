package com.royal.recreation.controller;

import com.royal.recreation.config.bean.MyUserDetails;
import com.royal.recreation.controller.base.BaseController;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.spring.mongo.Mongo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Objects;

@Controller
@RequestMapping("/safe")
public class SafeController extends BaseController {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SafeController(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @RequestMapping("/info")
    public String counts() {
        return "safe/info";
    }

    @RequestMapping("/userInfo")
    @ResponseBody
    public Object userInfo(@AuthenticationPrincipal MyUserDetails userDetails) {
        try {
            UserInfo userInfo = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
            return userInfo.getPoint();
        } catch (Exception e) {
            return "error";
        }

    }

    @RequestMapping("/passwd")
    public String passwd(@AuthenticationPrincipal MyUserDetails userDetails, Model model) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        model.addAttribute("user", user);
        return "safe/passwd";
    }

    @RequestMapping("/setPasswd")
    @ResponseBody
    public Object setPasswd(@AuthenticationPrincipal MyUserDetails userDetails, String oldpassword, String newpassword, String qrpassword, HttpServletResponse response) {
        UserInfo user = Mongo.buildMongo().id(userDetails.getId(), UserInfo.class);
        if (!Objects.equals(passwordEncoder.encode(oldpassword), user.getPassword())) {
            return responseError("旧密码错误");
        }
        if (!Objects.equals(newpassword, qrpassword)) {
            return responseError("两次密码输入不一致");
        }
        Mongo.buildMongo().id(userDetails.getId()).updateFirst(update -> {
            update.set("password", passwordEncoder.encode(newpassword));
        }, UserInfo.class);
        return Collections.singletonMap("msg", "修改成功");
    }

}
