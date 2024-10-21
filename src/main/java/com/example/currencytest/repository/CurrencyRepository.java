package com.example.currencytest.repository;

import com.example.currencytest.model.Currency;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface CurrencyRepository extends R2dbcRepository<Currency, Integer> {

    Mono<Currency> findByCode(String code);

    Mono<Currency> findByCcy(String ccy);

}
