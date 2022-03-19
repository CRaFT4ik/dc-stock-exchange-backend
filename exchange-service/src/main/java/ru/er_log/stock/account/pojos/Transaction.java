package ru.er_log.stock.account.pojos;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedNativeQuery;
import javax.persistence.SqlResultSetMapping;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * This is not real entity and is only used to join two tables.
 * More info: https://stackoverflow.com/questions/25179180/jpa-joining-two-tables-in-non-entity-class/25184489#25184489
 */
@Entity
@SqlResultSetMapping(
        name = "transaction_dto",
        classes = @ConstructorResult(
                targetClass = Transaction.class,
                columns = {
                        @ColumnResult(name = "uid", type = UUID.class),
                        @ColumnResult(name = "price", type = BigDecimal.class),
                        @ColumnResult(name = "amount", type = BigDecimal.class),
                        @ColumnResult(name = "timestamp", type = Long.class),
                        @ColumnResult(name = "is_active", type = Boolean.class),
                        @ColumnResult(name = "type", type = String.class)
                }
        )
)
@NamedNativeQuery(
        name = "get_user_transactions",
        query = "SELECT COALESCE(lf.id, lr.id)                                     as uid, " +
                "       COALESCE(lf.price, lr.price)                               as price, " +
                "       COALESCE(lf.amount, lr.amount)                             as amount, " +
                "       COALESCE(lf.timestamp_created, lr.timestamp_created)       as timestamp, " +
                "       COALESCE(lf.is_active, lr.is_active)                       as is_active, " +
                "       (CASE WHEN lr.price IS NULL THEN 'offer' ELSE 'order' END) as type " +
                "FROM lot_offers lf " +
                "         FULL JOIN lot_orders lr ON lr.id = lf.id " +
                "WHERE lf.user_id = :userId " +
                "   OR lr.user_id = :userId " +
                "ORDER BY timestamp DESC " +
                "LIMIT :limit OFFSET :offset",
        resultSetMapping = "transaction_dto"
)
public class Transaction {
    @Id
    @Column(nullable = false)
    public UUID id;
    public BigDecimal price;
    public BigDecimal amount;
    public long timestamp;
    public boolean is_active;
    public String type;

    public Transaction(UUID id, BigDecimal price, BigDecimal amount, long timestamp, boolean is_active, String type) {
        this.id = id;
        this.price = price;
        this.amount = amount;
        this.timestamp = timestamp;
        this.is_active = is_active;
        this.type = type;
    }

    public Transaction() {
    }
}
