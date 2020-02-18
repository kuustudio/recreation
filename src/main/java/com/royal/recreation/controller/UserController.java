package com.royal.recreation.controller;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.type.PointRecordType;
import com.royal.recreation.util.Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Random;

@Controller
@RequestMapping("/user")
public class UserController {

    private final DefaultKaptcha defaultKaptcha;

    @Autowired
    public UserController(DefaultKaptcha defaultKaptcha) {
        this.defaultKaptcha = defaultKaptcha;
    }

    @RequestMapping("/nickname")
    public String nickname() {
        return "user/nickname";
    }

    @RequestMapping("/login")
    public String login() {
        return "user/login";
    }

    @RequestMapping("/logout")
    public String logout() {
        return "redirect:/user/login";
    }

    @RequestMapping("/vcode/{vcodeKey}")
    public void vcode(@PathVariable String vcodeKey, HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setDateHeader("Expires", 0);
        // Set standard HTTP/1.1 no-cache headers.
        response.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
        // Set IE extended HTTP/1.1 no-cache headers (use addHeader).
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        // Set standard HTTP/1.0 no-cache header.
        response.setHeader("Pragma", "no-cache");
        // return a jpeg
        response.setContentType("image/jpeg");
        Random random = new Random();
        String text = String.format("%d%d%d%d", random.nextInt(10), random.nextInt(10), random.nextInt(10), random.nextInt(10));
        request.getSession().setAttribute("vcode", text);
        BufferedImage bi = defaultKaptcha.createImage(text);
        ServletOutputStream out = response.getOutputStream();
        ImageIO.write(bi, "jpg", out);
    }

}
