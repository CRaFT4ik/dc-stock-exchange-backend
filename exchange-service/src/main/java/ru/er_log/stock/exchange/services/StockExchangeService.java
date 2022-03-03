package ru.er_log.stock.exchange.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.er_log.stock.exchange.models.LotOffer;
import ru.er_log.stock.exchange.models.LotOrder;
import ru.er_log.stock.exchange.models.LotTransactions;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockExchangeService {

    private final Logger LOG = LoggerFactory.getLogger(StockExchangeService.class);

    private final LotOffersRepository lotOffersRepository;
    private final LotOrdersRepository lotOrdersRepository;
    private final LotTransactionsRepository lotTransactionsRepository;

    @Autowired
    public StockExchangeService(
            LotOffersRepository lotOffersRepository,
            LotOrdersRepository lotOrdersRepository,
            LotTransactionsRepository lotTransactionsRepository
    ) {
        this.lotOffersRepository = lotOffersRepository;
        this.lotOrdersRepository = lotOrdersRepository;
        this.lotTransactionsRepository = lotTransactionsRepository;
    }

    @Scheduled(initialDelay = 10 * 1000, fixedDelay = 15 * 1000)
    public void makeDeals() {
        LOG.info("- - - - - - - - - - - - - - - -");
        LOG.info("Preparing to handling deals...");
        try {
            makeDealsImpl();
        } catch (Exception e) {
            LOG.error("Error while executing scheduled task", e);
        }
    }

    private void makeDealsImpl() {
        final Pair<List<LotOrder>, List<LotOffer>> ordersAndOffers = getOrdersAndOffers();
        List<LotOrder> orders = ordersAndOffers.getFirst();
        List<LotOffer> offers = ordersAndOffers.getSecond();
        final List<LotTransactions> deals = new ArrayList<>();

        LOG.info("Handling deals. Total orders lots: {}. Total offers lots: {}", orders.size(), offers.size());

        final int totalOrders = orders.size();
        final int totalOffers = offers.size();
        final long timestamp = System.currentTimeMillis();

        for (LotOrder order : orders) {
            Iterator<LotOffer> offerIterator = offers.iterator();

            while (offerIterator.hasNext()) {
                LotOffer offer = offerIterator.next();

                // Checks if we find something with the good price for this order lot.
                // Otherwise, breaks operation because orders are ordered by price.
                if (offer.getPrice().compareTo(order.getPrice()) > 0) {
                    continue;
                }

                // Finds the offer lot with user != user in the order lot.
                if (offer.getUser().getId().equals(order.getUser().getId())) {
                    continue;
                }

                // Makes the deal.
                deals.add(new LotTransactions(offer, order, timestamp));
                offerIterator.remove();
                break;
            }
        }

        if (deals.size() > 0) {
            saveTransactions(deals);
        }
        outResults(totalOrders, totalOffers, deals.size());
    }

    @Transactional
    protected Pair<List<LotOrder>, List<LotOffer>> getOrdersAndOffers() {
        // In sort first parameter happens later.
        Sort sortOrders = Sort.by(Sort.Order.desc("price"), Sort.Order.asc("timestampCreated"));
        List<LotOrder> orders = lotOrdersRepository.findByIsActiveTrue(sortOrders);

        Sort sortOffers = Sort.by(Sort.Order.desc("price"), Sort.Order.asc("timestampCreated"));
        List<LotOffer> offers = lotOffersRepository.findByIsActiveTrue(sortOffers);

        return Pair.of(orders, offers);
    }

    @Transactional
    protected void saveTransactions(List<LotTransactions> deals) {
        lotTransactionsRepository.saveAll(deals);

        List<LotOrder> orders = deals.stream().map(LotTransactions::getLotOrder).collect(Collectors.toList());
        orders.forEach(e -> e.setActive(false));
        lotOrdersRepository.saveAll(orders);

        List<LotOffer> offers = deals.stream().map(LotTransactions::getLotOffer).collect(Collectors.toList());
        offers.forEach(e -> e.setActive(false));
        lotOffersRepository.saveAll(offers);
    }

    private void outResults(int totalOrders, int totalOffers, int served) {
        int notHandledOrders = totalOrders - served;
        int notHandledOffers = totalOffers - served;
        LOG.info("Handling deals completed. " +
                        "Deals carried out: {}. Not handled order lots: {}. Not handled offer lots: {}",
                served, notHandledOrders, notHandledOffers);
    }
}
