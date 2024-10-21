package com.example.currencytest.dto;

import lombok.*;
import lombok.experimental.Accessors;


@Getter
@Setter
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrencyDto {
    private Integer id;
    private String code;
    private String ccy;
    private String ccyNm_UZ;
    private String nominal;
    private Double rate;
    private String diff;
    private String date;
}
