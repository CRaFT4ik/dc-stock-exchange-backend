package ru.er_log.stock.exchange.pojos;

import ru.er_log.stock.exchange.models.LotTransactions;
import ru.er_log.stock.exchange.pojos.entities.Lot;

import java.util.List;
import java.util.stream.Collectors;

public class DealsResponse {

    public final List<Deal> deals;

    public DealsResponse(List<LotTransactions> deals) {
        this.deals = deals.stream().map(Deal::new).collect(Collectors.toList());
    }

    private static class Deal {
        public final Lot lotOrder;
        public final Lot lotOffer;
        public final long timestampCreated;

        public Deal(LotTransactions lotTransactions) {
            this.lotOrder = new Lot(lotTransactions.getLotOrder());
            this.lotOffer = new Lot(lotTransactions.getLotOffer());
            this.timestampCreated = lotTransactions.getTimestampCreated();
        }
    }
}
