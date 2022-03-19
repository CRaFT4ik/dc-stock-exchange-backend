package ru.er_log.stock.exchange.pojos;

import java.util.List;

public class OrderBookResponse {

    public final List<Lot> orders;
    public final List<Lot> offers;

    public OrderBookResponse(List<Lot> orders, List<Lot> offers) {
        this.orders = orders;
        this.offers = offers;
    }
}
