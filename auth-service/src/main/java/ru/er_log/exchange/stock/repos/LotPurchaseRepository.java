package ru.er_log.exchange.stock.repos;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.exchange.stock.models.LotPurchase;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotPurchaseRepository extends JpaRepository<LotPurchase, UUID> {

    List<LotPurchase> findByIsActiveTrue();

    List<LotPurchase> findByIsActiveTrue(Sort sort);
}
