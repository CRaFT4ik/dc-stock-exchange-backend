package ru.er_log.stock.exchange.repos;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.exchange.enities.LotOrder;
import ru.er_log.stock.exchange.pojos.Lot;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface LotOrdersRepository extends JpaRepository<LotOrder, UUID> {
    long deleteByUser_Id(Long id);

    long countByIsActiveTrue();

    List<LotOrder> findByIsActiveTrue(Sort sort);

    @Async
    @Query(value =
            "SELECT new ru.er_log.stock.exchange.pojos.Lot(u.price, SUM(u.amount)) " +
                    "FROM LotOrder u " +
                    "WHERE u.isActive = true " +
                    "GROUP BY u.price ORDER BY u.price")
    CompletableFuture<List<Lot>> findOrdersAmountByThisPrice(Pageable pageable);

    CompletableFuture<Long> countByUser_IdEqualsAndIsActiveTrue(Long id);

    CompletableFuture<Long> countByUser_IdEqualsAndIsActiveFalse(Long id);
}
