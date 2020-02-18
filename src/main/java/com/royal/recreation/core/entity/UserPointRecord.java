package com.royal.recreation.core.entity;

import com.royal.recreation.core.entity.base.Domain;
import com.royal.recreation.core.type.PointRecordType;
import lombok.Data;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;

/**
 * 积分记录
 */
@Data
@Document(collection = "user_point_record")
public class UserPointRecord extends Domain {

    @Indexed
    private String userId;
    private PointRecordType pointRecordType;
    private BigDecimal value;
    private BigDecimal balance;
    private String remark;
    private String businessId;

}
