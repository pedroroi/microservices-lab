package com.exemplo.order.dto;

import java.math.BigDecimal;

public record OrderResponse(
        String orderNumber,
        String skuCode,
        BigDecimal price,
        Integer quantity
) {
}
