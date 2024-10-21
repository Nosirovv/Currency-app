package com.example.currencytest;

import com.example.currencytest.dto.CurrencyDto;
import com.example.currencytest.model.Currency;
import com.example.currencytest.repository.CurrencyRepository;
import com.example.currencytest.service.serviceInter.CurrencyProvider;
import com.example.currencytest.service.serviceInter.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;


@AutoConfigureWebTestClient
@SpringBootTest(properties = "spring.profiles.active=test")
@ActiveProfiles("test")
public class CurrencyServiceTest {

    private static final String BASE_URL = "/api/test";
    private static final String SYNC_URL = BASE_URL + "/sync";
    private static final String CONVERT_URL = BASE_URL + "/convert";
    private static final String SEARCH_BY_NAME = BASE_URL + "/searchName";
    private static final String SEARCH_BY_CODE = BASE_URL + "/searchCode";
    private static final String ALL_RATES = BASE_URL + "/rates";

    @MockBean
    private CurrencyProvider currencyProvider;
    @Autowired
    private CurrencyRepository currencyRepository;
    @Autowired
    private CurrencyService currencyService;
    @Autowired
    private WebTestClient webTestClient;
    private Currency usdCurrency;
    private Currency rubCurrency;


    @BeforeEach
    void setUp() {
        currencyRepository.deleteAll().block();
        init();
        currencyRepository.save(rubCurrency.setId(null)).block();
        currencyRepository.save(usdCurrency.setId(null)).block();
    }

    @Test
    void sync_401() {
        webTestClient
                .put()
                .uri(SYNC_URL)
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void sync_403() {
        webTestClient
                .put()
                .uri(SYNC_URL)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass")
                )
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void sync() {
        when(currencyProvider.fetchAll()).thenReturn(Flux.just(usdCurrency, rubCurrency));
        Long sizeBefore = currencyRepository.count().block();
        webTestClient
                .put()
                .uri(SYNC_URL)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "adminpass"))
                .exchange()
                .expectStatus().isOk();
        Long sizeAfter = currencyRepository.count().block();

        assert sizeBefore != null;
        assertThat(sizeAfter).isEqualTo(sizeBefore + 1);
    }

    @Test
    void syncWithEmptyRepo() {
        currencyRepository.deleteAll().block();
        when(currencyProvider.fetchAll()).thenReturn(Flux.just(usdCurrency, rubCurrency));
        Long sizeBefore = currencyRepository.count().block();
        webTestClient
                .put()
                .uri(SYNC_URL)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "adminpass"))
                .exchange()
                .expectStatus().isOk();
        Long sizeAfter = currencyRepository.count().block();

        assert sizeBefore != null;
        assertThat(sizeAfter).isEqualTo(sizeBefore + 3);
    }

    @Test
    void convert() {
        Double expectedAmount = 9620.97;

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CONVERT_URL)
                        .queryParam("from", "USD")
                        .queryParam("to", "RUB")
                        .queryParam("amount", "100")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(Double.class)
                .consumeWith(response -> {
                    Double actualAmount = response.getResponseBody();
                    assertNotNull(actualAmount);
                    assertEquals(expectedAmount, actualAmount);
                });
    }

    @Test
    void convertNotFound() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(CONVERT_URL)
                        .queryParam("from", "INVALID")
                        .queryParam("to", "INVALID")
                        .queryParam("amount", "100")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void ConvertUnauthorized() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/convert")
                        .queryParam("from", "USD")
                        .queryParam("to", "EUR")
                        .queryParam("amount", "100")
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void GetCurrencyByCcy() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_NAME)
                        .queryParam("name", "RUB")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CurrencyDto.class)
                .value(currency -> {
                    assertNotNull(currency);
                    assertEquals("RUB", currency.getCcy());
                    assertEquals("Rossiya rubli", currency.getCcyNm_UZ());
                });
    }

    @Test
    void GetCurrencyByCcy_NotFound() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_NAME)
                        .queryParam("name", "AAA")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void GetCurrencyByCcy_Unauthorized() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_NAME)
                        .queryParam("name", "USD")
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }


    @Test
    void GetCurrencyByCode() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_CODE)
                        .queryParam("code", "643")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isOk()
                .expectBody(CurrencyDto.class)
                .value(currency -> {
                    assertNotNull(currency);
                    assertEquals("RUB", currency.getCcy());
                    assertEquals("Rossiya rubli", currency.getCcyNm_UZ());
                });
    }

    @Test
    void GetCurrencyByCode_NotFound() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_CODE)
                        .queryParam("code", "111")
                        .build())
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isNotFound();
    }


    @Test
    void GetCurrencyByCode_Unauthorized() {
        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_BY_CODE)
                        .queryParam("code", "643")
                        .build())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    void testGetAllCurrencyRates() {
        webTestClient.get()
                .uri(ALL_RATES)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("admin", "adminpass"))
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(CurrencyDto.class)
                .value(currencyList -> assertThat(currencyList).isNotEmpty());
    }

    @Test
    void GetAllCurrencyRates_Forbidden() {
        webTestClient.get()
                .uri(ALL_RATES)
                .headers(httpHeaders -> httpHeaders.setBasicAuth("user", "userpass"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    void GetAllCurrencyRates_Unauthorized() {
        webTestClient.get()
                .uri(ALL_RATES)
                .exchange()
                .expectStatus().isUnauthorized();
    }


    void init() {
        usdCurrency = new Currency()
                .setId(1L)
                .setCode("840")
                .setCcy("USD")
                .setCcyNm_UZ("AQSh dollari")
                .setCcyNm_RU("Доллар США")
                .setCcyNm_UZC("AQSh dollari")
                .setCcyNm_EN("US Dollar")
                .setNominal("1")
                .setRate(12815.13)
                .setDiff("0")
                .setDate("18.10.2024");

        rubCurrency = new Currency()
                .setId(2L)
                .setCode("643")
                .setCcy("RUB")
                .setCcyNm_UZ("Rossiya rubli")
                .setCcyNm_RU("Российский рубль")
                .setCcyNm_UZC("Россия рубли")
                .setCcyNm_EN("Russian Ruble")
                .setNominal("1")
                .setRate(133.2)
                .setDiff("0.4")
                .setDate("18.10.2024");
    }

}







