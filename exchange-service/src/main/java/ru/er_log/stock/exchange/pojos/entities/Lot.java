package ru.er_log.stock.exchange.pojos.entities;

import ru.er_log.stock.exchange.models.LotOrder;
import ru.er_log.stock.exchange.models.LotOffer;

import java.math.BigDecimal;

public class Lot {
    public final BigDecimal price;
    public final long timestampCreated;
    public final User owner;

    public Lot(LotOrder source) {
        this.price = source.getPrice();
        this.timestampCreated = source.getTimestampCreated();
        this.owner = new User(source.getUser());
    }

    public Lot(LotOffer source) {
        this.price = source.getPrice();
        this.timestampCreated = source.getTimestampCreated();
        this.owner = new User(source.getUser());
    }
}
