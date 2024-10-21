package com.example.currencytest.controller;


import com.example.currencytest.dto.CurrencyDto;
import com.example.currencytest.service.serviceInter.CurrencyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class CurrencyRateController {

    private final CurrencyService currencyService;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/rates")
    public Flux<CurrencyDto> getAllCurrencyRates(){
        return currencyService.getAllCurrencyRates();
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/convert")
    public Mono<ResponseEntity<Double>> convertCurrency(@RequestParam String from, @RequestParam String to, @RequestParam double amount){
        return currencyService.convertCurrency(from,to,amount)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/sync")
    public Mono<Void> updateCurrencyDatabase(){
        currencyService.fetchAndSaveCurrencies();
        return Mono.empty();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/searchCode")
    public Mono<ResponseEntity<CurrencyDto>> getCurrencyByCode(@RequestParam String code){
        return currencyService.searchCurrencyByCode(code)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @PreAuthorize("hasRole('USER')")
    @GetMapping("/searchName")
    public Mono<ResponseEntity<CurrencyDto>> getCurrencyByCcy(@RequestParam String name){
        return currencyService.searchCurrencyByCcy(name)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


}
