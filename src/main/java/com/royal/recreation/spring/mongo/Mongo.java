package com.royal.recreation.spring.mongo;

import com.mongodb.WriteConcern;
import com.mongodb.client.result.UpdateResult;
import com.royal.recreation.spring.SpringApplicationContext;
import com.royal.recreation.spring.mongo.basic.RegexUtils;
import com.royal.recreation.spring.mongo.exception.QueryException;
import lombok.Data;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.geo.GeoResult;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Metrics;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.NearQuery;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.StringUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Mongo extends MongoQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(Mongo.class);

    private static final String ID = "uuid";

    private MongoTemplate mongoOperations;


    private Mongo() {
        super();
        mongoOperations = SpringApplicationContext.getBean("mongoTemplate");
    }


    public Mongo safe() {
        mongoOperations.setWriteConcern(WriteConcern.SAFE);
        return this;
    }

    public List<GeoResult> geoFind(Double lat, Double lng, Class clazz, String collectionName) {
        NearQuery nearQuery = NearQuery.near(lng, lat, Metrics.KILOMETERS);
        nearQuery.maxDistance(10);
        nearQuery.query(query);
        nearQuery.spherical(true);
        nearQuery.skip(query.getSkip());
        nearQuery.num(query.getSkip() + query.getLimit());
        GeoResults geoResults = mongoOperations.geoNear(nearQuery, clazz, collectionName);
        return  geoResults.getContent();
    }

    public Mongo arrayEqAll(String property, String... values){
        criteria.and(property).all(values);
        return this;
    }

    public Mongo multiEq(String property, Object... values) {
        Criteria[] criterias = new Criteria[values.length];
        int index = 0;
        for (Object str : values) {
            criterias[index] = Criteria.where(property).is(str);
            index++;
        }
        criteria.andOperator(criterias);
        return this;
    }


    private Mongo(String source) {
        super();
        mongoOperations = SpringApplicationContext.getBean(source);
    }

    public static Mongo buildMongo() {
        return new Mongo();
    }

    public static Mongo buildMongo(String source) {
        return new Mongo(source);
    }


    public Mongo mySelf(IMyselfQuery myself) {
        myself.query(criteria);
        return this;
    }

    public Mongo eq(String key, Object value) {
        and(key).is(value);
        return this;
    }

    public Mongo id(String value) {
        and("_id").is(new ObjectId(value));
        return this;
    }

    public <T> T id(String id, Class<T> clazz) {
        if (StringUtils.isEmpty(id)) {
            return null;
        }
        eq("_id", new ObjectId(id));
        return findOne(clazz);
    }

    public Mongo fuzzy(String key, Object value) {
        String re = String.valueOf(value);
        re = RegexUtils.compile(re);
        and(key).regex(re, "i");
        return this;
    }


    public Mongo ne(String key, Object value) {
        and(key).ne(value);
        return this;
    }

    public Mongo or(String key, Object... value) {
        criteria.orOperator(genOrCriteria(key, value));
        return this;
    }

    public Mongo or(String[] key, Object... value) {
        criteria.orOperator(genOrCriteria(key, value));
        return this;
    }

    public Mongo or(String[] key, Object[] value, String[] operators) {
        criteria.orOperator(genOrCriteria(key, value, operators));
        return this;
    }

    private Criteria[] genOrCriteria(String key, Object[] value) {
        Criteria[] criterias = new Criteria[value.length];
        for (int i = 0; i < value.length; i++) {
            criterias[i] = Criteria.where(key).is(value[i]);
        }
        return criterias;
    }

    private Criteria[] genOrCriteria(String[] key, Object[] value) {
        Criteria[] criterias = new Criteria[value.length];
        for (int i = 0; i < value.length; i++) {
            criterias[i] = Criteria.where(key[i]).is(value[i]);
        }
        return criterias;
    }

    private Criteria[] genOrCriteria(String[] key, Object[] value, String[] operators) {
        Criteria[] criterias = new Criteria[value.length];
        for (int i = 0; i < value.length; i++) {
            if ("in".equals(operators[i])) {
                String[] tempStr = (String[]) value[i];
                criterias[i] = Criteria.where(key[i]).in(tempStr);
            }
            if ("f".equals(operators[i])) {
                String re = String.valueOf(value[i]);
                re = re.replace("(", "\\(").replace(")", "\\)");
                criterias[i] = Criteria.where(key[i]).regex(re, "i");
            }
            if ("eq".equals(operators[i])) {
                criterias[i] = Criteria.where(key[i]).is(value[i]);
            }
            if ("gte".equals(operators[i])) {
                criterias[i] = Criteria.where(key[i]).gte(value[i]);
            }
            if ("ne".equals(operators[i])) {
                criterias[i] = Criteria.where(key[i]).ne(value[i]);
            }
        }
        return criterias;
    }

    public Mongo gte(String key, Object value) {
        and(key).gte(value);
        return this;
    }

    public Mongo gt(String key, Object value) {
        and(key).gt(value);
        return this;
    }

    public Mongo lt(String key, Object value) {
        and(key).lt(value);
        return this;
    }

    public Mongo lte(String key, Object value) {
        and(key).lte(value);
        return this;
    }


    public Mongo in(String key, Object... value) {
        and(key).in(value);
        return this;
    }

    public Mongo nin(String key, Object... value) {
        and(key).nin(value);
        return this;
    }

    public Mongo size(String key, int size) {
        and(key).size(size);
        return this;
    }

    public Mongo exists(String key, boolean flag) {
        and(key).exists(flag);
        return this;
    }

    public Mongo limit(int limit, int page) {
        if (page < 1) {
            throw new RuntimeException("page is invalid ...");
        }
        query.limit(limit);
        query.skip((page - 1) * limit);
        return this;
    }

    public Mongo limitSkip(int limit, int skip) {
        if (skip < 0) {
            throw new RuntimeException("skip is invalid ...");
        }
        query.limit(limit);
        query.skip(skip);
        return this;
    }

    public Mongo desc(String... properties) {
        query.with(Sort.by(Sort.Direction.DESC, properties));
        return this;
    }

    public Mongo asc(String... properties) {
        query.with(Sort.by(Sort.Direction.ASC, properties));
        return this;
    }

    public Mongo type(String key, int type) {
        and(key).type(type);
        return this;
    }

    public <T> T load(String uuid, Class<T> clazz, String collectionName) {
        return eq(ID, uuid).findOne(clazz, collectionName);
    }

    public enum MongoType {
        UPDATE, INSERT
    }

    public static String collectionName(Object obj) {
        Class clazz = obj.getClass();
        return collectionName(clazz);
    }

    public static String collectionName(Class clazz) {
        if (clazz.isAnnotationPresent(Document.class)) {
            Document table = (Document) clazz.getAnnotation(Document.class);
            return table.collection();
        } else {
            return clazz.getSimpleName();
        }
    }

    public MongoType insert(IMongoUpdate update, Object obj) {
        return insert(update, obj, collectionName(obj));
    }

    public MongoType insert(IMongoUpdate update, Object obj, String collectionName) {
        if (count(collectionName) > 0) {
            if (update != null) {
                updateFirst(update, collectionName);
            }
            return MongoType.UPDATE;
        } else {
            insert(obj, collectionName);
            return MongoType.INSERT;
        }
    }

    public UpdateResult upsert(IMongoUpdate update, String collectionName) {
        Update update1 = new Update();
        update.update(update1);
        return mongoOperations.upsert(query, update1, collectionName);
    }

    public UpdateResult updateMulti(IMongoUpdate update, Class clazz) {
        return updateMulti(update, collectionName(clazz));
    }

    public UpdateResult updateMulti(IMongoUpdate update, String collectionName) {
        Update update1 = new Update();
        update.update(update1);
        return mongoOperations.updateMulti(query, update1, collectionName);
    }

    public void updateFirst(IMongoUpdate update, String collectionName) {
        Update update1 = new Update();
        update.update(update1);
        mongoOperations.updateFirst(query, update1, collectionName);
    }

    public void updateFirst(IMongoUpdate update, Object obj) {
        updateFirst(update, collectionName(obj));
    }

    public void updateFirst(IMongoUpdate update, Class clazz) {
        updateFirst(update, collectionName(clazz));
    }

    public void remove(Class clazz) {
        mongoOperations.remove(query, collectionName(clazz));
    }
    public void remove(String collectionName) {
        mongoOperations.remove(query, collectionName);
    }


    public long count(String collectionName) {
        return mongoOperations.count(query, collectionName);
    }

    public long count(Class clazz) {
        return mongoOperations.count(query, collectionName(clazz));
    }

    public <T> List<T> find(Class<T> clazz) {
        return mongoOperations.find(query, clazz, collectionName(clazz));
    }

    public <T> T findAndUpdate(Class clazz, IMongoUpdate update) {
        Update update1 = new Update();
        update.update(update1);
        return (T) mongoOperations.findAndModify(query, update1, clazz, collectionName(clazz));
    }


    public Long sum(String sum, Class clazz, String... group) {
        return new MyAggregation<Long>() {
            @Override
            public GroupOperation.GroupOperationBuilder nextGroupOperation(GroupOperation groupOperation, String key) {
                return groupOperation.sum(key);
            }

            @Override
            public Long valueOf(String value) {
                return Double.valueOf(value).longValue();
            }
        }.handler(sum, collectionName(clazz), group);
    }

    public List<GroupResult> sumGroup(String sum, Class clazz, String... group) {
        GroupOperation groupOperation = new GroupOperation(Fields.fields(group));
        AggregationResults aggregationResults = mongoOperations.aggregate(Aggregation.newAggregation(new MatchOperation(criteria), groupOperation.sum(sum).as("value")), clazz, new GroupResult<BigDecimal>().getClass());
        return aggregationResults.getMappedResults();
    }

    @Data
    public static class GroupResult<T> {
        private String id;
        private T value;
    }



/*    public List geo(int page, int limit, double lng, double lat, Class clazz) {
        try {
            System.out.println(query.getSkip());
            System.out.println(query.getLimit());
//            PageRequest pageRequest = new PageRequest(page, limit, Sort.Direction.DESC, "createAt");
            NearQuery nearQuery = NearQuery.near(lat, lng, Metrics.KILOMETERS).maxDistance(10).query(query).spherical(true).num(query.getLimit()).skip(query.getSkip()).with(pageRequest);
//            MyGeoNearOperation geoNearOperation = new MyGeoNearOperation(nearQuery);
//            AggregationResults aggregationResults = mongoOperations.aggregate(Aggregation.newAggregation(geoNearOperation), collectionName(clazz), HashMap.class);
            GeoResults geoResults = mongoOperations.geoNear(nearQuery, clazz, collectionName(clazz));
            return geoResults.;
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }*/


    public GroupOperation fillFields(Class clazz, GroupOperation groupOperation) {
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            groupOperation = groupOperation.first(field.getName()).as(field.getName());
        }
        fields = clazz.getSuperclass().getDeclaredFields();
        for (Field field : fields) {
            groupOperation = groupOperation.first(field.getName()).as(field.getName());

        }
        return groupOperation;
    }

    public Double avg(String avg, Class clazz, String... group) {
        return new MyAggregation<Double>() {
            @Override
            public GroupOperation.GroupOperationBuilder nextGroupOperation(GroupOperation groupOperation, String key) {
                return groupOperation.avg(key);
            }

            @Override
            public Double valueOf(String value) {
                return Double.valueOf(value);
            }
        }.handler(avg, collectionName(clazz), group);

    }

    private abstract class MyAggregation<T extends Number> {
        public T handler(String key, String collectionName, String... group) {
            String valueKey = "_" + key;
            GroupOperation groupOperation = new GroupOperation(Fields.fields(group));
            AggregationResults aggregationResults = mongoOperations.aggregate(Aggregation.newAggregation(new MatchOperation(criteria), nextGroupOperation(groupOperation, key).as(valueKey)), collectionName, HashMap.class);
            List<Map> maps = aggregationResults.getMappedResults();
            Double total = 0d;
            for (Map map : maps) {
                total += Double.valueOf(String.valueOf(map.get(valueKey)));
            }
            return valueOf(String.valueOf(total));
        }

        public abstract T valueOf(String value);

        public abstract GroupOperation.GroupOperationBuilder nextGroupOperation(GroupOperation groupOperation, String key);
    }


    public List find(Class clazz, String collectionName) {
        LOGGER.debug(query.toString());
        return mongoOperations.find(query, clazz, collectionName);
    }

    public <T> T findOne(Class<T> clazz) {
        return mongoOperations.findOne(query, clazz, collectionName(clazz));
    }

    public <T> T findOne(Class clazz, String collectionName) {
        return (T) mongoOperations.findOne(query, clazz, collectionName);
    }

    public org.bson.Document executeCommand(String json) {
        return mongoOperations.executeCommand(json);
    }

    public void insert(Object obj) {
        insert(obj, collectionName(obj));
    }

    public void insert(Object obj, String collectionName) {
        mongoOperations.insert(obj, collectionName);
    }

    public void insertAll(Collection<? extends Object> objects) {
        mongoOperations.insertAll(objects);
    }

    public List findAll(Class clazz, String collectionName) {
        return mongoOperations.findAll(clazz, collectionName);
    }

    public List findAll(Class clazz) {
        return mongoOperations.findAll(clazz, collectionName(clazz));
    }

    public void ensureIndex(String name, Order order, String collectionName) {
        org.bson.Document dbObject = new org.bson.Document();
        dbObject.put(name, order.getOrderValue());
        mongoOperations.getCollection(collectionName).createIndex(dbObject);
    }

    public void ensureIndex2D(String name, String collectionName) {
        org.bson.Document dbObject = new org.bson.Document();
        dbObject.put(name, "2d");
        mongoOperations.getCollection(collectionName).createIndex(dbObject);
    }


    public Mongo between(String key, Object begin, Object end, Between between) {
        between.between(and(key), begin, end);
        return this;
    }

    public enum Order {
        desc(-1), asc(1);

        private int orderValue;

        Order(int orderValue) {
            this.orderValue = orderValue;
        }

        public int getOrderValue() {
            return orderValue;
        }
    }

    public enum Between {
        EQ, NEQ, FEQ, EEQ;

        public void between(Criteria criteria, Object begin, Object end) {
            switch (this) {
                case EQ:
                    criteria.lte(end).gte(begin);
                    break;
                case NEQ:
                    criteria.lt(end).gt(begin);
                    break;
                case FEQ:
                    criteria.lt(end).gte(begin);
                    break;
                case EEQ:
                    criteria.lte(end).gt(begin);
                    break;
                default:
                    throw new QueryException("no Between enum");
            }
        }
    }
}