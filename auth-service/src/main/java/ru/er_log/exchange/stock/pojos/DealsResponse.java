package ru.er_log.exchange.stock.pojos;

import ru.er_log.exchange.stock.models.DealsByLots;
import ru.er_log.exchange.stock.pojos.entities.Lot;

import java.util.List;
import java.util.stream.Collectors;

public class DealsResponse {

    public final List<Deal> deals;

    public DealsResponse(List<DealsByLots> deals) {
        this.deals = deals.stream().map(Deal::new).collect(Collectors.toList());
    }

    private static class Deal {
        public final Lot lotPurchase;
        public final Lot lotSale;
        public final long timestampCreated;

        public Deal(DealsByLots dealsByLots) {
            this.lotPurchase = new Lot(dealsByLots.getLotPurchase());
            this.lotSale = new Lot(dealsByLots.getLotSale());
            this.timestampCreated = dealsByLots.getTimestampCreated();
        }
    }
}
