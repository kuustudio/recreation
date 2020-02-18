package com.royal.recreation.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/cash")
public class CashController {


    @RequestMapping("/rechargeLog")
    public String counts() {
        return "cash/rechargeLog";
    }

    @RequestMapping("/toCashLog")
    public String toCashLog() {
        return "cash/toCashLog";
    }

}
