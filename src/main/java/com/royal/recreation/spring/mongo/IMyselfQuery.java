package com.royal.recreation.spring.mongo;

import org.springframework.data.mongodb.core.query.Criteria;


public interface IMyselfQuery {
    void query(Criteria criteria);
}
