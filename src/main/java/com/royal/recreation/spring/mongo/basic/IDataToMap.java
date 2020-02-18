package com.royal.recreation.spring.mongo.basic;

import java.util.Map;


public interface IDataToMap<T> {
    public void dataToMap(Map<Object, Object> map, T source);
}