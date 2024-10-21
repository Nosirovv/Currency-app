package com.example.currencytest.service.serviceInter;

import com.example.currencytest.dto.CurrencyDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CurrencyService {

    void fetchAndSaveCurrencies();

    Flux<CurrencyDto> getAllCurrencyRates();

    Mono<Double> convertCurrency(String fromCurrency, String toCurrency, double amount);

    Mono<CurrencyDto> searchCurrencyByCode(String code);

    Mono<CurrencyDto> searchCurrencyByCcy(String name);

}
