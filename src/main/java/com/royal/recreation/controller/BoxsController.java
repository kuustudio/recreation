package com.royal.recreation.controller;

import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/Boxs")
public class BoxsController {

    @RequestMapping("/fixTime")
    public String fixTime() {
        List<UserPointRecord> recordList = Mongo.buildMongo().eq("time", null).find(UserPointRecord.class);
        for (UserPointRecord userPointRecord : recordList) {
            Mongo.buildMongo().id(userPointRecord.getId()).updateFirst(update -> {
                update.set("time", DateUtil.dateTimeToTime(userPointRecord.getCreateAt()));
            }, UserPointRecord.class);
        }
        return "Boxs/receive";
    }

    @RequestMapping("/receive")
    public String counts() {
        return "Boxs/receive";
    }

    @RequestMapping("/searchReceive")
    public String searchReceive() {
        return "Boxs/searchReceive";
    }

}
