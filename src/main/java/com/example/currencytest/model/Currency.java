package com.example.currencytest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.Accessors;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@ToString
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "currency")
public class Currency {

    @Id
    @JsonProperty("id")
    private Long id;

    @JsonProperty("Code")
    private String code;

    @JsonProperty("Ccy")
    private String ccy;

    @JsonProperty("CcyNm_UZ")
    private String ccyNm_UZ;

    @JsonProperty("CcyNm_RU")
    private String ccyNm_RU;

    @JsonProperty("CcyNm_UZC")
    private String ccyNm_UZC;

    @JsonProperty("CcyNm_EN")
    private String ccyNm_EN;

    @JsonProperty("Nominal")
    private String nominal;

    @JsonProperty("Rate")
    private Double rate;

    @JsonProperty("Diff")
    private String diff;

    @JsonProperty("Date")
    private String date;

}