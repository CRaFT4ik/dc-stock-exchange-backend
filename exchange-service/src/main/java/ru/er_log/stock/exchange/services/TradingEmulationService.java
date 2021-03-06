package ru.er_log.stock.exchange.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.er_log.stock.auth.enities.User;
import ru.er_log.stock.auth.repos.UserRepository;
import ru.er_log.stock.exchange.enities.LotOffer;
import ru.er_log.stock.exchange.enities.LotOrder;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.DoubleStream;

@Service
@Transactional
public class TradingEmulationService {

    private final Logger LOG = LoggerFactory.getLogger(TradingEmulationService.class);

    private final User offersServiceUser, ordersServiceUser;
    private final LotOffersRepository lotOffersRepository;
    private final LotOrdersRepository lotOrdersRepository;
    private final LotTransactionsRepository lotTransactionsRepository;

    private final EmulationLotGenerator offersGenerator;
    private final EmulationLotGenerator ordersGenerator;

    private static final int totalGenerated = 100;
    private static final int triggerGenerationLotsCount = 60;

    @Autowired
    public TradingEmulationService(
            LotOffersRepository lotOffersRepository,
            LotOrdersRepository lotOrdersRepository,
            LotTransactionsRepository lotTransactionsRepository,
            UserRepository userRepository
    ) {
        this.lotOffersRepository = lotOffersRepository;
        this.lotOrdersRepository = lotOrdersRepository;
        this.lotTransactionsRepository = lotTransactionsRepository;

        this.offersServiceUser = userRepository.findByUsername("service_bot_1").orElse(null);
        this.ordersServiceUser = userRepository.findByUsername("service_bot_2").orElse(null);

        this.offersGenerator = new EmulationLotGenerator(offersServiceUser, 44000 - 5000, 0.24, 30);
        this.ordersGenerator = new EmulationLotGenerator(ordersServiceUser, 44000 + 5000, 0.21, 20);
    }

    /**
     * Every 15 minutes.
     */
    // @Transactional
    @Scheduled(initialDelay = 20 * 1000, fixedDelay = 15 * 60 * 1000)
    public void updateFakeData() {
        try {
            LOG.info("- - - - - - - - - - - -");
            LOG.info("Updating fake data ...");

            List<LotOrder> orders = generateOrdersIfNeeded();
            List<LotOffer> offers = generateOffersIfNeeded();

            lotOrdersRepository.saveAll(orders);
            lotOffersRepository.saveAll(offers);

            LOG.info("Added {} fake orders and {} fake offers", orders.size(), offers.size());
        } catch (Exception e) {
            LOG.error("Error while executing scheduled task", e);
        }
    }

    /**
     * Removes old offers and creates new daily.
     * Every day at 01:00 AM.
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void dailyUpdateFakeData() {
        try {
            LOG.info("Daily cleaning of old lots ...");
            cleanActiveLots();
            updateFakeData();

            // LOG.info("Daily cleaning of fake data ...");
            // cleanFakeData(offersServiceUser.getId(), ordersServiceUser.getId());
        } catch (Exception e) {
            LOG.error("Error while executing scheduled task", e);
        }
    }

    private void cleanActiveLots() {
        long deleted = lotOffersRepository.deleteByIsActiveTrue();
        LOG.info("Deleted {} old active offer lots", deleted);

        deleted = lotOrdersRepository.deleteByIsActiveTrue();
        LOG.info("Deleted {} old active order lots", deleted);
    }

    // @Transactional
    protected void cleanFakeData(long offersServiceUserId, long ordersServiceUserId) {
        long deleted = lotTransactionsRepository.deleteByLotOffer_User_IdAndLotOrder_User_Id(
                offersServiceUserId, ordersServiceUserId);
        LOG.info("Deleted {} old fake transactions from database", deleted);

        deleted = lotOffersRepository.deleteByUser_Id(offersServiceUserId);
        LOG.info("Deleted {} old fake active offer lots", deleted);

        deleted = lotOrdersRepository.deleteByUser_Id(ordersServiceUserId);
        LOG.info("Deleted {} old fake active order lots", deleted);
    }

    private List<LotOffer> generateOffersIfNeeded() {
        long existCount = lotOffersRepository.countByIsActiveTrue();
        if (existCount > triggerGenerationLotsCount) return List.of();

        int neededCount = (int) (totalGenerated - existCount);
        return offersGenerator.generateOffers(neededCount);
    }

    private List<LotOrder> generateOrdersIfNeeded() {
        long existCount = lotOrdersRepository.countByIsActiveTrue();
        if (existCount > triggerGenerationLotsCount) return List.of();

        int neededCount = (int) (totalGenerated - existCount);
        return ordersGenerator.generateOrders(neededCount);
    }

    private static class EmulationLotGenerator {

        private final Random random;
        private final double priceBound;
        private final int maxLotAmount;
        private final double priceDeviation;
        private final User lotOwner;

        /**
         * @param lotOwner       owner of generating lots
         * @param priceBound     lower price bound (for offers) and upper price bound (for orders)
         * @param priceDevFactor deviation from {@code exchangeRate} (from 0.0 to 1.0)
         * @param maxLotAmount   max amount value for each generating lot
         */
        public EmulationLotGenerator(User lotOwner, double priceBound, double priceDevFactor, int maxLotAmount) {
            this.lotOwner = lotOwner;
            this.priceBound = priceBound;
            this.priceDeviation = priceBound * priceDevFactor;
            this.maxLotAmount = maxLotAmount;

            this.random = new Random();
        }

        public List<LotOffer> generateOffers(int totalGenerate) {
            long timestamp = System.currentTimeMillis();
            List<LotOffer> result = new ArrayList<>(totalGenerate);

            double eps = priceBound * 0.003;
            DoubleStream stream = random.doubles(totalGenerate, priceBound + eps, priceBound + priceDeviation);

            stream.forEach(priceLong -> {
                BigDecimal amount = BigDecimal.valueOf(nextExponential() * maxLotAmount);
                LotOffer offer = new LotOffer(BigDecimal.valueOf(priceLong), amount, lotOwner, timestamp);
                result.add(offer);
            });
            return result;
        }

        public List<LotOrder> generateOrders(int totalGenerate) {
            long timestamp = System.currentTimeMillis();
            List<LotOrder> result = new ArrayList<>(totalGenerate);

            double eps = priceBound * 0.003;
            DoubleStream stream = random.doubles(totalGenerate, priceBound - priceDeviation, priceBound - eps);

            stream.forEach(priceLong -> {
                BigDecimal amount = BigDecimal.valueOf(nextExponential() * maxLotAmount);
                LotOrder order = new LotOrder(BigDecimal.valueOf(priceLong), amount, lotOwner, timestamp);
                result.add(order);
            });
            return result;
        }

        /**
         * @return exponential convex value from 0.0 to 1.0
         */
        private double nextExponential() {
            double multiplier = 200;
            return Math.log(1 + random.nextDouble() * multiplier) / Math.log(1 + multiplier);
        }
    }
}
