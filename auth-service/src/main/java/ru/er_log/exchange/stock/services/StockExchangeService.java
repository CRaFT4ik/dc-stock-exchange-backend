package ru.er_log.exchange.stock.services;

import org.hibernate.criterion.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import ru.er_log.exchange.auth.configs.jwt.AuthEntryPointJwt;
import ru.er_log.exchange.stock.models.LotPurchase;
import ru.er_log.exchange.stock.models.LotSale;
import ru.er_log.exchange.stock.repos.DealsByLotsRepository;
import ru.er_log.exchange.stock.repos.LotPurchaseRepository;
import ru.er_log.exchange.stock.repos.LotSaleRepository;

import java.util.List;
import java.util.Optional;

public class StockExchangeService {

    private final Logger LOG = LoggerFactory.getLogger(StockExchangeService.class);

    @Autowired
    LotSaleRepository lotSaleRepository;

    @Autowired
    LotPurchaseRepository lotPurchaseRepository;

    @Autowired
    DealsByLotsRepository dealsByLotsRepository;

    @Scheduled(initialDelay = 10000, fixedDelay = 10000)
    @Transactional
    public void makeDeals() {
        // TODO: check if sorted by timestamp firstly and by price secondly. It's important!
        List<LotPurchase> purchases = lotPurchaseRepository.findByIsActiveTrue(Sort.by(Sort.Direction.DESC, "price"));
        List<LotSale> sales = lotSaleRepository.findByIsActiveTrue(Sort.by(Sort.Order.asc("timestamp_created"), Sort.Order.desc("price")));

        final int totalPurchases = purchases.size();
        final int totalSales = sales.size();
        int servedPurchases = 0;

        for (LotPurchase purchase : purchases) {
            // e.getPrice() <= purchase.getPrice()
            Optional<LotSale> availableSales = sales.stream()
                    .filter(e -> e.getPrice().compareTo(purchase.getPrice()) <= 0)
                    .findFirst();
            if (availableSales.isEmpty()) {
                outResults(totalPurchases - servedPurchases, totalPurchases - servedPurchases);
                break;
            }

            servedPurchases++;
        }
    }

    private void outResults(int notHandledPurchases, int notHandledSales) {
        LOG.info("Handling deals completed. " +
                "Not handled purchase lots: " + notHandledPurchases + ". " +
                "Not handled sale lots: " + notHandledSales);
    }
}
