package com.example.currencytest.service;

import com.example.currencytest.model.Currency;
import com.example.currencytest.service.serviceInter.CurrencyProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(prefix = "currency-provider", name = "impl", havingValue = "cbu", matchIfMissing = true)
public class CurrencyProviderWithCbu implements CurrencyProvider {

    private final WebClient webClient;

    @Override
    public Flux<Currency> fetchAll() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Currency.class);
    }

}
