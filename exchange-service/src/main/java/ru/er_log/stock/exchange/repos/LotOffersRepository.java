package ru.er_log.stock.exchange.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.exchange.enities.LotOffer;
import ru.er_log.stock.exchange.pojos.Lot;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface LotOffersRepository extends JpaRepository<LotOffer, UUID> {
    long deleteByUser_Id(Long id);

    long deleteByIsActiveTrue();

    long countByIsActiveTrue();

    List<LotOffer> findByIsActiveTrue(Sort sort);

    @Async
    @Query(value =
            "SELECT new ru.er_log.stock.exchange.pojos.Lot(u.price, SUM(u.amount)) " +
                    "FROM LotOffer u " +
                    "WHERE u.isActive = true " +
                    "GROUP BY u.price ORDER BY u.price ASC")
    CompletableFuture<List<Lot>> findOffersAmountByThisPrice(Pageable pageable);

    CompletableFuture<Long> countByUser_IdEqualsAndIsActiveTrue(Long id);

    CompletableFuture<Long> countByUser_IdEqualsAndIsActiveFalse(Long id);
}
