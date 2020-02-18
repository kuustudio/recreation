package com.royal.recreation.spring.mongo;

import org.springframework.data.mongodb.core.query.Update;


public interface IMongoUpdate {
    void update(Update update);
}
