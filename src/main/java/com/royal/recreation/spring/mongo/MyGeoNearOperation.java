package com.royal.recreation.spring.mongo;

import org.bson.Document;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperationContext;
import org.springframework.data.mongodb.core.query.NearQuery;


public class MyGeoNearOperation implements AggregationOperation {

    private NearQuery nearQuery;

    public MyGeoNearOperation(NearQuery nearQuery) {
        this.nearQuery = nearQuery;
    }


    @Override
    public Document toDocument(AggregationOperationContext context) {
        Document dbObject = context.getMappedObject(nearQuery.toDocument());
        dbObject.put("distanceField", "distance");
        return new Document("$geoNear",dbObject);
    }
}
