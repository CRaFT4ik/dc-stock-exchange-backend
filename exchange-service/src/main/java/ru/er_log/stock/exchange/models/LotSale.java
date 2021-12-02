package ru.er_log.stock.exchange.models;

import ru.er_log.stock.auth.models.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "sale_lots")
public class LotSale {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private BigDecimal price;

    @ManyToOne(cascade = {CascadeType.REFRESH}, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "timestamp_created", nullable = false)
    private long timestampCreated;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    public LotSale() {
    }

    public LotSale(BigDecimal price, User user, long timestampCreated) {
        this.price = price;
        this.user = user;
        this.timestampCreated = timestampCreated;
        this.isActive = true;
    }

    public UUID getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public User getUser() {
        return user;
    }

    public long getTimestampCreated() {
        return timestampCreated;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
