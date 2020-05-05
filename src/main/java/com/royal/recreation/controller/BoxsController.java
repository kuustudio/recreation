package com.royal.recreation.controller;

import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.core.entity.UserPointRecord;
import com.royal.recreation.core.entity.inner.AwardDetail;
import com.royal.recreation.core.type.Status;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.DateUtil;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/Boxs")
public class BoxsController {

    @RequestMapping("query")
    @ResponseBody
    public Object query() {
        List<OrderInfo> orderInfoList = Mongo.buildMongo().eq("time", DateUtil.dateTimeToTime(LocalDateTime.now().minusDays(1))).eq("status", Status.ACTIVE).find(OrderInfo.class);
        BigDecimal total = BigDecimal.ZERO;
        StringBuilder sb = new StringBuilder();
        for (OrderInfo orderInfo : orderInfoList) {
            List<AwardDetail> details = orderInfo.getAwardDetails();
            Optional<AwardDetail> awardDetails = details.stream().filter(a -> a.getAwardUserId().equals("5eab80d299ae231b7f0122a0")).findFirst();
            if (awardDetails.isPresent()) {
                AwardDetail detail = awardDetails.get();
                int i = details.indexOf(detail);
                sb.append(String.format("%s,%s,%s,%s,%s,%s,%s",
                        orderInfo.getUsername(), orderInfo.getOrderNo(), orderInfo.getUsePoint(), detail.getAwardMoney(), detail.getAwardRate(), i>0?details.get(i-1).getAwardRate():BigDecimal.ZERO, orderInfo.getCreateAt())).append(System.lineSeparator());
                total = total.add(detail.getAwardMoney());
            }
        }
        return sb.toString();
    }

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
