package ru.er_log.exchange.stock.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.exchange.stock.models.DealsByLots;

import java.util.UUID;

@Repository
public interface DealsByLotsRepository extends JpaRepository<DealsByLots, UUID> {
}
