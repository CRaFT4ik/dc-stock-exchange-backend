package ru.er_log.stock.exchange.repos;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.er_log.stock.exchange.models.LotOffer;

import java.util.List;
import java.util.UUID;

@Repository
public interface LotOffersRepository extends JpaRepository<LotOffer, UUID> {
    long deleteByUser_Id(Long id);

    long countByIsActiveTrue();

    List<LotOffer> findByIsActiveTrue();

    List<LotOffer> findByIsActiveTrue(Sort sort);
}
