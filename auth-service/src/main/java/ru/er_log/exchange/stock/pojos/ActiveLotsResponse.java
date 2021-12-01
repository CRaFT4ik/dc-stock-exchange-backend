package ru.er_log.exchange.stock.pojos;

import ru.er_log.exchange.stock.models.LotPurchase;
import ru.er_log.exchange.stock.models.LotSale;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

public class ActiveLotsResponse {

    public final List<Lot> lotPurchases;
    public final List<Lot> lotSales;

    public ActiveLotsResponse(List<LotPurchase> lotPurchases, List<LotSale> lotSales) {
        this.lotPurchases = lotPurchases.stream().map(Lot::new).collect(Collectors.toList());
        this.lotSales = lotSales.stream().map(Lot::new).collect(Collectors.toList());
    }

    private static class Lot {
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
}
