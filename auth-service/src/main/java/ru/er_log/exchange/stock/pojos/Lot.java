package ru.er_log.exchange.stock.pojos;

import ru.er_log.exchange.stock.models.LotPurchase;
import ru.er_log.exchange.stock.models.LotSale;

import java.math.BigDecimal;

public class Lot {
    public final BigDecimal price;
    public final long timestampCreated;

    public Lot(LotPurchase source) {
        price = source.getPrice();
        timestampCreated = source.getTimestampCreated();
    }

    public Lot(LotSale source) {
        price = source.getPrice();
        timestampCreated = source.getTimestampCreated();
    }
}
