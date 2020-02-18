package com.royal.recreation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/zst")
public class ZstController {

    @RequestMapping("/")
    public String index(Integer typeid) {
        if (typeid == null) {

        }
        return "zst/index";
    }

}
