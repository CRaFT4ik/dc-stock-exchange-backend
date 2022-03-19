package ru.er_log.stock.exchange.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.account.pojos.Transaction;
import ru.er_log.stock.exchange.enities.LotTransactions;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Repository
public interface LotTransactionsRepository extends JpaRepository<LotTransactions, UUID> {
    long deleteByLotOffer_User_IdAndLotOrder_User_Id(Long lotOfferUserId, Long lotOrderUserId);

    @Async
    @Query(value =
            "SELECT SUM(lo.price * lo.amount) FROM LotOrder lo " +
                    "WHERE lo.user.id = ?1 AND lo.isActive = FALSE " +
                    "AND lo.id IN (SELECT lt.lotOrder.id FROM LotTransactions lt)")
    CompletableFuture<BigDecimal> ordersTransactionsBalance(long userId);

    @Async
    @Query(value =
            "SELECT SUM(lo.price * lo.amount) FROM LotOffer lo " +
                    "WHERE lo.user.id = ?1 AND lo.isActive = FALSE " +
                    "AND lo.id IN (SELECT lt.lotOffer.id FROM LotTransactions lt)")
    CompletableFuture<BigDecimal> offersTransactionsBalance(long userId);

    /**
     * @return future of all user's orders and offers.
     */
    @Async
    @Query(name = "get_user_transactions", nativeQuery = true)
    CompletableFuture<List<Transaction>> allUserTransactions(
            @Param("userId") long userId,
            @Param("limit") int limit,
            @Param("offset") int offset
    );
}

