package ru.er_log.stock.exchange.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.er_log.stock.exchange.enities.LotOffer;
import ru.er_log.stock.exchange.enities.LotOrder;
import ru.er_log.stock.exchange.enities.LotTransactions;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
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

    /**
     * Every 30 seconds.
     */
    @Scheduled(initialDelay = 5 * 1000, fixedDelay = 30 * 1000)
    public void makeDeals() {
        LOG.info("- - - - - - - - - - - - - - - -");
        LOG.info("Preparing to handling deals...");
        try {
            final Pair<List<LotOrder>, List<LotOffer>> ordersAndOffers = getOrdersAndOffers();
            final List<LotOrder> orders = ordersAndOffers.getFirst();
            final List<LotOffer> offers = ordersAndOffers.getSecond();
            final List<LotTransactions> deals = new ArrayList<>();

            final List<LotOrder> createdOrders = new ArrayList<>();
            final List<LotOffer> createdOffers = new ArrayList<>();

            final int totalOrders = orders.size();
            final int totalOffers = offers.size();

            LOG.info("Handling deals. Total order lots: {}. Total offer lots: {}", totalOrders, totalOffers);
            makeDealsImpl(orders, offers, deals, createdOrders, createdOffers);

            if (deals.size() > 0) {
                saveTransactions(deals, createdOrders, createdOffers);
            }

            int served = deals.size();
            LOG.info("Handling deals completed. Transactions made: {}", served);
        } catch (Exception e) {
            LOG.error("Error while executing scheduled task", e);
        }
    }

    /**
     * Constraints:
     *  1. Orders must be sorted: desc(price) and then asc(created time)
     *  2. Offers must be sorted: asc(created time) and then asc(price)
     */
    private void makeDealsImpl(
            List<LotOrder> orders,
            List<LotOffer> offers,
            List<LotTransactions> deals,
            List<LotOrder> createdOrders,
            List<LotOffer> createdOffers
    ) {
        final long timestamp = System.currentTimeMillis();

        for (LotOrder order : orders) {
            BigDecimal leftOrderAmount = order.getAmount();
            ListIterator<LotOffer> offersIterator = offers.listIterator();

            while (offersIterator.hasNext()) {
                LotOffer offer = offersIterator.next();

                if (offer.getPrice().compareTo(order.getPrice()) > 0) {
                    break;
                } else if (offer.getUser().getId().equals(order.getUser().getId())) {
                    continue;
                }

                createdOrders.remove(order);
                createdOffers.remove(offer);

                BigDecimal offerAmount = offer.getAmount();
                int amountCompare = offerAmount.compareTo(leftOrderAmount);
                if (amountCompare < 0) { // offer.amount < order.amount
                    // Creating left part of order.
                    leftOrderAmount = leftOrderAmount.subtract(offerAmount);
                    LotOrder leftOrder = new LotOrder(order.getPrice(), leftOrderAmount, order.getUser(), order.getTimestampCreated());
                    createdOrders.add(leftOrder);

                    // Handling current order.
                    order.setAmount(offerAmount);
                    deals.add(new LotTransactions(offer, order, timestamp));

                    order = leftOrder;
                    offersIterator.remove();
                } else if (amountCompare > 0) { // offer.amount > order.amount
                    // Creating left part of offer.
                    BigDecimal leftOfferAmount = offerAmount.subtract(leftOrderAmount);
                    LotOffer leftOffer = new LotOffer(offer.getPrice(), leftOfferAmount, offer.getUser(), offer.getTimestampCreated());
                    createdOffers.add(leftOffer);
                    offersIterator.set(leftOffer);

                    // Handling current offer.
                    offer.setAmount(leftOrderAmount);
                    deals.add(new LotTransactions(offer, order, timestamp));
                    break;
                } else { // offer.amount == order.amount
                    deals.add(new LotTransactions(offer, order, timestamp));

                    offersIterator.remove();
                    break;
                }
            }
        }
    }

    // @Transactional
    protected Pair<List<LotOrder>, List<LotOffer>> getOrdersAndOffers() {
        // In sort first parameter happens later.
        Sort sortOrders = Sort.by(Sort.Order.asc("timestampCreated"), Sort.Order.desc("price"));
        List<LotOrder> orders = lotOrdersRepository.findByIsActiveTrue(sortOrders);

        Sort sortOffers = Sort.by(Sort.Order.asc("price"), Sort.Order.asc("timestampCreated"));
        List<LotOffer> offers = lotOffersRepository.findByIsActiveTrue(sortOffers);

        return Pair.of(orders, offers);
    }

    // @Transactional (wtf not working)
    protected void saveTransactions(List<LotTransactions> deals, List<LotOrder> createdOrders, List<LotOffer> createdOffers) {
        Set<LotOrder> orders = deals.stream().map(LotTransactions::getLotOrder).collect(Collectors.toSet());
        orders.forEach(e -> e.setActive(false));
        lotOrdersRepository.saveAll(orders);

        Set<LotOffer> offers = deals.stream().map(LotTransactions::getLotOffer).collect(Collectors.toSet());
        offers.forEach(e -> e.setActive(false));
        lotOffersRepository.saveAll(offers);

        lotOrdersRepository.saveAll(createdOrders);
        lotOffersRepository.saveAll(createdOffers);
        lotTransactionsRepository.saveAll(deals);
    }
}
