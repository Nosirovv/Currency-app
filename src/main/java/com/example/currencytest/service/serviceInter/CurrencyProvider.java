package com.example.currencytest.service.serviceInter;

import com.example.currencytest.model.Currency;
import reactor.core.publisher.Flux;

public interface CurrencyProvider {

    Flux<Currency> fetchAll();

}
