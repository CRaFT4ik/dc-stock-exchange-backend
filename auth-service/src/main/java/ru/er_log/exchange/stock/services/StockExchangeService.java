package ru.er_log.exchange.stock.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.er_log.exchange.stock.models.DealsByLots;
import ru.er_log.exchange.stock.models.LotPurchase;
import ru.er_log.exchange.stock.models.LotSale;
import ru.er_log.exchange.stock.repos.DealsByLotsRepository;
import ru.er_log.exchange.stock.repos.LotPurchaseRepository;
import ru.er_log.exchange.stock.repos.LotSaleRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockExchangeService {

    private final Logger LOG = LoggerFactory.getLogger(StockExchangeService.class);

    @Autowired
    LotSaleRepository lotSaleRepository;

    @Autowired
    LotPurchaseRepository lotPurchaseRepository;

    @Autowired
    DealsByLotsRepository dealsByLotsRepository;

    @Scheduled(initialDelay = 10000, fixedDelay = 60000)
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
        final Pair<List<LotPurchase>, List<LotSale>> purchasesAndSales = getPurchasesAndSales();
        List<LotPurchase> purchases = purchasesAndSales.getFirst();
        List<LotSale> sales = purchasesAndSales.getSecond();
        final List<DealsByLots> deals = new ArrayList<>();

        LOG.info("Handling deals. Total purchase lots: {}. Total sale lots: {}", purchases.size(), sales.size());

        final int totalPurchases = purchases.size();
        final int totalSales = sales.size();
        final long timestamp = System.currentTimeMillis();

        u:
        for (LotPurchase purchase : purchases) {
            Iterator<LotSale> saleIterator = sales.iterator();
            while (saleIterator.hasNext()) {
                LotSale sale = saleIterator.next();

                // Check if we find something with the good price for this purchase lot.
                // Otherwise, breaks operation because purchases are ordered by price.
                if (sale.getPrice().compareTo(purchase.getPrice()) > 0) {
                    break u;
                }

                // Finding sale lot with user != user in purchase lot.
                if (!sale.getUser().getId().equals(purchase.getUser().getId())) {
                    deals.add(new DealsByLots(sale, purchase, timestamp));
                    saleIterator.remove();
                    break;
                }
            }
        }

        if (deals.size() > 0) {
            saveTransactions(deals);
        }
        outResults(totalPurchases, totalSales, deals.size());
    }

    @Transactional
    protected Pair<List<LotPurchase>, List<LotSale>> getPurchasesAndSales() {
        // First parameter happens later.
        Sort sortPurchases = Sort.by(Sort.Order.asc("price"), Sort.Order.asc("timestampCreated"));
        List<LotPurchase> purchases = lotPurchaseRepository.findByIsActiveTrue(sortPurchases);

        Sort sortSales = Sort.by(Sort.Order.asc("price"), Sort.Order.asc("timestampCreated"));
        List<LotSale> sales = lotSaleRepository.findByIsActiveTrue(sortSales);

        return Pair.of(purchases, sales);
    }

    @Transactional
    protected void saveTransactions(List<DealsByLots> deals) {
        dealsByLotsRepository.saveAll(deals);

        List<LotPurchase> purchases = deals.stream().map(DealsByLots::getLotPurchase).collect(Collectors.toList());
        purchases.forEach(e -> e.setActive(false));
        lotPurchaseRepository.saveAll(purchases);

        List<LotSale> sales = deals.stream().map(DealsByLots::getLotSale).collect(Collectors.toList());
        sales.forEach(e -> e.setActive(false));
        lotSaleRepository.saveAll(sales);
    }

    private void outResults(int totalPurchases, int totalSales, int served) {
        int notHandledPurchases = totalPurchases - served;
        int notHandledSales = totalSales - served;
        LOG.info("Handling deals completed. " +
                        "Deals carried out: {}. Not handled purchase lots: {}. Not handled sale lots: {}",
                served, notHandledPurchases, notHandledSales);
    }
}
