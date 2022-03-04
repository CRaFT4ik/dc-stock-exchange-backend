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
@Table(name = "lot_transactions",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"lot_offer_id", "lot_order_id"})
        })
public class LotTransactions {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private UUID id;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "lot_offer_id", nullable = false)
    private LotOffer lotOffer;

    @OneToOne(cascade = CascadeType.ALL, optional = false)
    @JoinColumn(name = "lot_order_id", nullable = false)
    private LotOrder lotOrder;

    @Column(name = "timestamp_created", nullable = false)
    private long timestampCreated;

    public LotTransactions() {
    }

    public LotTransactions(LotOffer lotOffer, LotOrder lotOrder, long timestampCreated) {
        this.lotOffer = lotOffer;
        this.lotOrder = lotOrder;
        this.timestampCreated = timestampCreated;
    }

    public UUID getId() {
        return id;
    }

    public LotOrder getLotOrder() {
        return lotOrder;
    }

    public LotOffer getLotOffer() {
        return lotOffer;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }
}
