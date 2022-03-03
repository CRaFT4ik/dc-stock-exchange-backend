package ru.er_log.stock.exchange.pojos;

import ru.er_log.stock.exchange.models.LotOrder;
import ru.er_log.stock.exchange.models.LotOffer;
import ru.er_log.stock.exchange.pojos.entities.Lot;

import java.util.List;
import java.util.stream.Collectors;

public class ActiveLotsResponse {

    public final List<Lot> lotOrders;
    public final List<Lot> lotOffers;

    public ActiveLotsResponse(List<LotOrder> lotOrders, List<LotOffer> lotOffers) {
        this.lotOrders = lotOrders.stream().map(Lot::new).collect(Collectors.toList());
        this.lotOffers = lotOffers.stream().map(Lot::new).collect(Collectors.toList());
    }
}
