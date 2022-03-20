package ru.er_log.stock.account.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.er_log.stock.account.pojos.UserCard;
import ru.er_log.stock.auth.enities.User;
import ru.er_log.stock.auth.pojos.MessageResponse;
import ru.er_log.stock.auth.repos.UserRepository;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/account")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AccountController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    LotTransactionsRepository transactionsRepository;

    @Autowired
    LotOrdersRepository ordersRepository;

    @Autowired
    LotOffersRepository offersRepository;

    @GetMapping("/profile")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> fetchProfile(HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            var futureOrdersTransactionsBalance = transactionsRepository.ordersTransactionsBalance(currentUser.getId());
            var futureOffersTransactionsBalance = transactionsRepository.offersTransactionsBalance(currentUser.getId());

            var futureActiveOrdersCount = ordersRepository.countByUser_IdEqualsAndIsActiveTrue(currentUser.getId());
            var futureCompletedOrdersCount = ordersRepository.countByUser_IdEqualsAndIsActiveFalse(currentUser.getId());

            var futureActiveOffersCount = offersRepository.countByUser_IdEqualsAndIsActiveTrue(currentUser.getId());
            var futureCompletedOffersCount = offersRepository.countByUser_IdEqualsAndIsActiveFalse(currentUser.getId());

            var offersTransactionsBalance = futureOffersTransactionsBalance.get();
            if (offersTransactionsBalance == null) offersTransactionsBalance = BigDecimal.ZERO;

            var ordersTransactionsBalance = futureOrdersTransactionsBalance.get();
            if (ordersTransactionsBalance == null) ordersTransactionsBalance = BigDecimal.ZERO;

            var userInfo = new UserCard.UserInfo(currentUser.getUsername(), currentUser.getEmail());
            var userBalance = offersTransactionsBalance.subtract(ordersTransactionsBalance);
            var transactionStatistics = new UserCard.TransactionStatistics(
                    futureCompletedOrdersCount.get(),
                    futureActiveOrdersCount.get(),
                    futureCompletedOffersCount.get(),
                    futureActiveOffersCount.get()
            );
            var userCard = new UserCard(userInfo, userBalance, transactionStatistics);

            return ResponseEntity.ok(userCard);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }

    @GetMapping("/transactions")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> fetchTransactions(HttpServletRequest request) {
        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            var futureTransactions = transactionsRepository.allUserTransactions(currentUser.getId(), 50, 0);
            return ResponseEntity.ok(futureTransactions.get());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
