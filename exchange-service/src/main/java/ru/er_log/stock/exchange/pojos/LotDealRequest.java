package ru.er_log.stock.exchange.pojos;

import java.math.BigDecimal;

public class LotDealRequest {
    private BigDecimal price;

    private BigDecimal amount;

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
