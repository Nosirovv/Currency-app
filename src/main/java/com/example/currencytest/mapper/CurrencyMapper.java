package com.example.currencytest.mapper;

import com.example.currencytest.dto.CurrencyDto;
import com.example.currencytest.model.Currency;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {

    CurrencyDto toDto(Currency currency);

    Currency toEntity(CurrencyDto currencyDto);

}
