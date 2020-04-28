package com.royal.recreation.core.entity;


import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.UserType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;


@Data
@Document(collection = "user_info")
public class UserInfo extends Domain {
    // 账号
    @Indexed
    private String username;
    private String wxNo;
    private String password;
    private UserType userType;
    private String bonusSettingId;
    private String bonusSettingName;
    private Boolean print = Boolean.FALSE;
    @Indexed
    private String pId;
    private BigDecimal point = BigDecimal.ZERO;

    public Boolean getPrint() {
        return print == null ? Boolean.FALSE : print;
    }
}