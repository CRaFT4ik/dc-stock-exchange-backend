package ru.er_log.stock.account.pojos;

import java.math.BigDecimal;

public class UserCard {

    public final UserInfo userInfo;
    public final BigDecimal userBalance;
    public final TransactionStatistics transactionStatistics;

    public UserCard(UserInfo userInfo, BigDecimal userBalance, TransactionStatistics transactionStatistics) {
        this.userInfo = userInfo;
        this.userBalance = userBalance;
        this.transactionStatistics = transactionStatistics;
    }

    public static class UserInfo {
        public final String userName;
        public final String userEmail;

        public UserInfo(String userName, String userEmail) {
            this.userName = userName;
            this.userEmail = userEmail;
        }
    }

    public static class TransactionStatistics {
        public final long ordersCompleted;
        public final long ordersActive;
        public final long offersCompleted;
        public final long offersActive;

        public TransactionStatistics(long ordersCompleted, long ordersActive, long offersCompleted, long offersActive) {
            this.ordersCompleted = ordersCompleted;
            this.ordersActive = ordersActive;
            this.offersCompleted = offersCompleted;
            this.offersActive = offersActive;
        }
    }
}
