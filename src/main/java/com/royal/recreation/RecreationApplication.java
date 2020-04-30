package com.royal.recreation;

import com.royal.recreation.core.entity.BonusSetting;
import com.royal.recreation.core.entity.CapitalPool;
import com.royal.recreation.core.entity.UserInfo;
import com.royal.recreation.core.type.BonusLimitType;
import com.royal.recreation.core.type.UserType;
import com.royal.recreation.spring.mongo.Mongo;
import com.royal.recreation.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.HashMap;

@SpringBootApplication
public class RecreationApplication implements CommandLineRunner {

    @Autowired
    PasswordEncoder passwordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(RecreationApplication.class, args);
    }

    @Override
    public void run(String... args) {
        BonusSetting bonusSetting = Mongo.buildMongo().findOne(BonusSetting.class);
        if (bonusSetting == null) {
            bonusSetting = new BonusSetting();
            bonusSetting.setSettingName("管理员专用");
            bonusSetting.setA1(BigDecimal.valueOf(2));
            bonusSetting.setA2(BigDecimal.valueOf(10));
            bonusSetting.setA3(BigDecimal.valueOf(10));
            bonusSetting.setA4(BigDecimal.valueOf(100));
            bonusSetting.setA5(BigDecimal.valueOf(15.84));
            bonusSetting.setA6(BigDecimal.valueOf(2.6));
            bonusSetting.setA7(BigDecimal.valueOf(2.1));
            bonusSetting.setA9(BigDecimal.valueOf(2.6));
            bonusSetting.setA10(BigDecimal.valueOf(2.6));
            bonusSetting.setA11(BigDecimal.valueOf(2.6));
            bonusSetting.setA12(BigDecimal.valueOf(2.6));
            bonusSetting.setA13(BigDecimal.valueOf(2.6));
            bonusSetting.setA14(BigDecimal.valueOf(2.6));
            bonusSetting.setA15(BigDecimal.valueOf(2.6));
            bonusSetting.setA16(BigDecimal.valueOf(2.6));
            bonusSetting.setA17(BigDecimal.valueOf(2.6));
            bonusSetting.setFanDianRate(new BigDecimal("0.1"));
            bonusSetting.setAwardRate(new BigDecimal("0.1"));
            Mongo.buildMongo().insert(bonusSetting);
        }
        // 初始化庄家
        UserInfo adminUser = Mongo.buildMongo().eq("userType", UserType.ADMIN).findOne(UserInfo.class);
        if (adminUser == null) {
            adminUser = new UserInfo();
            adminUser.setUsername("meng8888");
            adminUser.setWxNo("meng8888");
            adminUser.setPassword(passwordEncoder.encode("12qw34er"));
            adminUser.setUserType(UserType.ADMIN);
            adminUser.setBonusSettingId(bonusSetting.getId());
            adminUser.setBonusSettingName(bonusSetting.getSettingName());
            Mongo.buildMongo().insert(adminUser);
        }
        // 初始化奖金池
        CapitalPool pool = Mongo.buildMongo().findOne(CapitalPool.class);
        if (pool == null) {
            pool = new CapitalPool();
            Mongo.buildMongo().insert(pool);
        }
        if (CollectionUtils.isEmpty(pool.getSystemLimit())) {
            Mongo.buildMongo().id(pool.getId()).updateFirst(update -> {
                update.set("systemLimit", new HashMap<BonusLimitType, BigDecimal>() {{
                    for (BonusLimitType value : BonusLimitType.values()) {
                        put(value, value.getLimit());
                    }
                }});
            }, CapitalPool.class);
        }
        pool = Mongo.buildMongo().findOne(CapitalPool.class);
        Constant.SYSTEM_NAME = pool.getSystemName();
        Constant.SYSTEM_MAX_BET_POINT = pool.getSystemMaxBetPoint();
        Constant.SYSTEM_NOTICE = pool.getSystemNotice();
        Constant.SYSTEM_LIMIT = pool.getSystemLimit();
    }
}
