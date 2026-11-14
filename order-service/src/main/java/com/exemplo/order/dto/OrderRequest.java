package com.exemplo.order.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record OrderRequest(
       @NotBlank
       String skuCode,
       @NotNull @Positive
       BigDecimal price,
       @NotNull @Positive
       Integer quantity
) {
}
