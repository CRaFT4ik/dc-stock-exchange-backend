package ru.er_log.stock.exchange.models;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
public class DealsByLots {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private UUID id;

    @OneToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "lot_sale_id", nullable = false)
    private LotSale lotSale;

    @OneToOne(cascade = CascadeType.REFRESH, optional = false)
    @JoinColumn(name = "lot_purchase_id", nullable = false)
    private LotPurchase lotPurchase;

    @Column(name = "timestamp_created", nullable = false)
    private long timestampCreated;

    public DealsByLots() {
    }

    public DealsByLots(LotSale lotSale, LotPurchase lotPurchase, long timestampCreated) {
        this.lotSale = lotSale;
        this.lotPurchase = lotPurchase;
        this.timestampCreated = timestampCreated;
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

    public long getTimestampCreated() {
        return timestampCreated;
    }
}
