package ru.er_log.exchange.stock.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.util.UUID;

@Entity
@Table(name = "deals_by_lots",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = "lot_sale_id"),
                @UniqueConstraint(columnNames = "lot_purchase_id")
        })
public class LotDeal {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    @OneToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "lot_sale_id", nullable = false)
    private LotSale lotSale;

    @OneToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "lot_purchase_id", nullable = false)
    private LotPurchase lotPurchase;

    public LotDeal() {
    }

    public LotDeal(LotSale lotSale, LotPurchase lotPurchase) {
        this.lotSale = lotSale;
        this.lotPurchase = lotPurchase;
    }

    public UUID getId() {
        return id;
    }

    public LotPurchase getLotPurchase() {
        return lotPurchase;
    }

    public LotSale getLotSale() {
        return lotSale;
    }
}
