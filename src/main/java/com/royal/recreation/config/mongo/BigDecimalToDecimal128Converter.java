package com.royal.recreation.config.mongo;

import org.bson.types.Decimal128;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

import java.math.BigDecimal;

/**
 * 向mongo中写数据时,默认是String,改为Decimal128
 */
@WritingConverter
public class BigDecimalToDecimal128Converter implements Converter<BigDecimal, Decimal128> {

    @Override
    public Decimal128 convert(BigDecimal source) {
        return new Decimal128(source);
    }
}
