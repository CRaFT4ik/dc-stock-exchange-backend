package ru.er_log.stock.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.er_log.stock.auth.enities.User;
import ru.er_log.stock.auth.pojos.MessageResponse;
import ru.er_log.stock.auth.repos.UserRepository;
import ru.er_log.stock.auth.services.UserDetailsServiceImpl;
import ru.er_log.stock.exchange.enities.LotOffer;
import ru.er_log.stock.exchange.enities.LotOrder;
import ru.er_log.stock.exchange.pojos.Lot;
import ru.er_log.stock.exchange.pojos.OrderBookResponse;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/exchange")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StockExchangeController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LotOffersRepository lotOffersRepository;

    @Autowired
    LotOrdersRepository lotOrdersRepository;

    @Autowired
    LotTransactionsRepository lotTransactionsRepository;

    @PostMapping("/order")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> createOrder(HttpServletRequest request, @RequestBody Lot creatingLot) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            LotOrder lotOrder = new LotOrder(
                    creatingLot.price, creatingLot.amount, currentUser, System.currentTimeMillis());
            lotOrdersRepository.save(lotOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/offer")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> createOffer(HttpServletRequest request, @RequestBody Lot creatingLot) {
        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            LotOffer lotOffer = new LotOffer(
                    creatingLot.price, creatingLot.amount, currentUser, System.currentTimeMillis());
            lotOffersRepository.save(lotOffer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/order_book")
    public ResponseEntity<?> fetchOrderBook(@RequestParam(defaultValue = "100") Integer limit) {
        try {
            var page = PageRequest.of(0, Math.min(limit, 1000));

            var ordersFuture = lotOrdersRepository.findOrdersAmountByThisPrice(page);
            var offersFuture = lotOffersRepository.findOffersAmountByThisPrice(page);

            List<Lot> orders = ordersFuture.get();
            List<Lot> offers = offersFuture.get();

            int minIndex = Math.min(orders.size(), offers.size());
            OrderBookResponse response = new OrderBookResponse(
                    orders.subList(0, minIndex),
                    offers.subList(0, minIndex)
            );

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }
    }
}
