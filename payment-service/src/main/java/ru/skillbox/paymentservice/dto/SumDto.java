package ru.skillbox.paymentservice.dto;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class SumDto {

    @Min(1)
    private Integer sum;
}
