package ru.er_log.stock.exchange.pojos;

import ru.er_log.stock.exchange.enities.LotOrder;
import ru.er_log.stock.exchange.enities.LotOffer;

import java.math.BigDecimal;

public class Lot {
    public BigDecimal price;
    public BigDecimal amount;

    public Lot(BigDecimal price, BigDecimal amount) {
        this.price = price;
        this.amount = amount;
    }

    public Lot(LotOrder source) {
        this.price = source.getPrice();
        this.amount = source.getAmount();
//        this.timestampCreated = source.getTimestampCreated();
//        this.owner = new User(source.getUser());
    }

    public Lot(LotOffer source) {
        this.price = source.getPrice();
        this.amount = source.getAmount();
//        this.timestampCreated = source.getTimestampCreated();
//        this.owner = new User(source.getUser());
    }

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
