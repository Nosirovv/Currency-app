package com.example.currencytest.service;

import com.example.currencytest.dto.CurrencyDto;
import com.example.currencytest.mapper.CurrencyMapper;
import com.example.currencytest.model.Currency;
import com.example.currencytest.repository.CurrencyRepository;
import com.example.currencytest.service.serviceInter.CurrencyProvider;
import com.example.currencytest.service.serviceInter.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {


    private final CurrencyRepository currencyRepository;
    private final CurrencyProvider currencyProvider;
    private final CurrencyMapper currencyMapper;
    private final WebClient webClient;


    @Override
    public void fetchAndSaveCurrencies() {
        currencyProvider.fetchAll()
                .concatWithValues(getUzsCurrency())
                .flatMap(currency ->
                        currencyRepository.findByCode(currency.getCode())
                                .map(existingCurrency -> update(currency, existingCurrency))
                                .flatMap(currencyRepository::save)
                                .switchIfEmpty(Mono.defer(() -> currencyRepository.save(currency.setId(null))))
                )
                .subscribe(result -> System.out.println("Valyuta saqlandi: " + result),
                        error -> System.err.println("Xato: " + error.getMessage()));
    }

    private Currency update(Currency newCurrency, Currency existingCurrency) {
        return existingCurrency.setDate(newCurrency.getDate())
                .setRate(newCurrency.getRate())
                .setDiff(newCurrency.getDiff());
    }

    private Currency getUzsCurrency() {
        return new Currency()
                .setCode("860")
                .setCcy("UZS")
                .setCcyNm_UZ("O'zbekiston so'mi")
                .setCcyNm_RU("Сум")
                .setCcyNm_UZC("Узбекский сум")
                .setCcyNm_EN("soum")
                .setNominal("1")
                .setRate(1.00)
                .setDiff("0.00")
                .setDate(LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
    }

    @Override
    public Flux<CurrencyDto> getAllCurrencyRates() {
        return currencyRepository.findAll()
                .map(currencyMapper::toDto);
    }

    @Override
    public Mono<Double> convertCurrency(String from, String to, double amount) {
        return currencyRepository.findByCcy(from)
                .zipWith(currencyRepository.findByCcy(to))
                .map(tuple -> {
                    Currency fromCurrency = tuple.getT1();
                    Currency toCurrency = tuple.getT2();

                    double conversionRate = fromCurrency.getRate() / toCurrency.getRate();
                    double rate = amount * conversionRate;

                    return Math.round(rate * 100.0) / 100.0;
                });
    }

    @Override
    public Mono<CurrencyDto> searchCurrencyByCode(String code) {
        return currencyRepository.findByCode(code)
                .map(currencyMapper::toDto);
    }


    @Override
    public Mono<CurrencyDto> searchCurrencyByCcy(String name) {
        return currencyRepository.findByCcy(name)
                .map(currencyMapper::toDto);
    }

}
