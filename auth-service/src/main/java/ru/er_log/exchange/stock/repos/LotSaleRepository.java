package ru.er_log.exchange.stock.repos;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.exchange.stock.models.LotSale;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotSaleRepository extends JpaRepository<LotSale, UUID> {

    List<LotSale> findByIsActiveTrue(Sort sort);
}