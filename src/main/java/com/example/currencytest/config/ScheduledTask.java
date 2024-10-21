package com.example.currencytest.config;

import com.example.currencytest.service.CurrencyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ScheduledTask {

    private final CurrencyServiceImpl currencyService;

    @Scheduled(cron = "0 0 0 * * *")
    public void runFetchAndSaveTask() {
        currencyService.fetchAndSaveCurrencies();
    }

}
