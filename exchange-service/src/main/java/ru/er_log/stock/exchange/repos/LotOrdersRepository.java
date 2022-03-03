package ru.er_log.stock.exchange.repos;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.exchange.models.LotOrder;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotOrdersRepository extends JpaRepository<LotOrder, UUID> {

    List<LotOrder> findByIsActiveTrue();

    List<LotOrder> findByIsActiveTrue(Sort sort);
}
