package com.royal.recreation.config.mongo;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Mongo读数据时,Decimal128->BigDecimal
 */
@ReadingConverter
public class Decimal128ToBigDecimalConverter implements Converter<Decimal128, BigDecimal> {
    @Override
    public BigDecimal convert(Decimal128 source) {
        BigDecimal bigDecimal = source.bigDecimalValue();
        BigDecimal b1 = bigDecimal.stripTrailingZeros();
        if (b1.scale() < 2) {
            return b1.setScale(2, RoundingMode.HALF_UP);
        }
        return b1;
    }
}
