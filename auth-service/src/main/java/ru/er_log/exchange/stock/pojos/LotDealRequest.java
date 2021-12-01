package ru.er_log.exchange.stock.pojos;

import java.math.BigDecimal;

public class LotDealRequest {
    private BigDecimal price;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}
