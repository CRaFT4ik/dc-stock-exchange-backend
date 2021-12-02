package ru.er_log.stock.exchange.repos;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.exchange.models.DealsByLots;

import java.util.UUID;

@Repository
public interface DealsByLotsRepository extends JpaRepository<DealsByLots, UUID> {
}
