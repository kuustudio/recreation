package com.royal.recreation.util;

import com.royal.recreation.core.entity.OrderInfo;
import com.royal.recreation.spring.SpringApplicationContext;
import com.royal.recreation.spring.mongo.Mongo;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

@Component
public class SystemSetting implements InitializingBean {

    private static AtomicLong orderNoGenerator;


    private final SpringApplicationContext springApplicationContext;

    @Autowired
    public SystemSetting(SpringApplicationContext springApplicationContext) {
        this.springApplicationContext = springApplicationContext;
    }

    public static String getOrderNo() {
        return Long.toHexString(orderNoGenerator.getAndIncrement());
    }

    @Override
    public void afterPropertiesSet() {
        OrderInfo lastOrderInfo = Mongo.buildMongo().desc("orderNo").findOne(OrderInfo.class);
        setOrderNo(lastOrderInfo);
    }

    private synchronized void setOrderNo(OrderInfo lastOrderInfo) {
        long start;
        if (lastOrderInfo == null || lastOrderInfo.getOrderNo() == null) {
            start = 268435456;
        } else {
            start = Long.valueOf(lastOrderInfo.getOrderNo(), 16) + 1;
        }
        orderNoGenerator = new AtomicLong(start);
    }

}
