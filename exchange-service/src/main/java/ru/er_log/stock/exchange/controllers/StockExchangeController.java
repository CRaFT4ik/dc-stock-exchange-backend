package ru.er_log.stock.exchange.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.er_log.stock.auth.models.User;
import ru.er_log.stock.auth.pojos.MessageResponse;
import ru.er_log.stock.auth.repos.UserRepository;
import ru.er_log.stock.auth.services.UserDetailsServiceImpl;
import ru.er_log.stock.exchange.models.LotOffer;
import ru.er_log.stock.exchange.models.LotOrder;
import ru.er_log.stock.exchange.models.LotTransactions;
import ru.er_log.stock.exchange.pojos.ActiveLotsResponse;
import ru.er_log.stock.exchange.pojos.DealsResponse;
import ru.er_log.stock.exchange.pojos.LotDealRequest;
import ru.er_log.stock.exchange.repos.LotOffersRepository;
import ru.er_log.stock.exchange.repos.LotOrdersRepository;
import ru.er_log.stock.exchange.repos.LotTransactionsRepository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/exchange")
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

    @PostMapping("/buy")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> addLotBuy(HttpServletRequest request, @RequestBody LotDealRequest dealRequest) {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            LotOrder lotOrder = new LotOrder(dealRequest.getPrice(), dealRequest.getAmount(), currentUser,
                    System.currentTimeMillis());
            lotOrdersRepository.save(lotOrder);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sell")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> addLotSell(HttpServletRequest request, @RequestBody LotDealRequest dealRequest) {
        try {
            String username = request.getRemoteUser();
            if (username == null) {
                throw new UsernameNotFoundException("User has not been authenticated");
            }

            User currentUser = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Error, user with name '" + username + "' is not found"));

            LotOffer lotOffer = new LotOffer(dealRequest.getPrice(), dealRequest.getAmount(), currentUser,
                    System.currentTimeMillis());
            lotOffersRepository.save(lotOffer);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new MessageResponse(e.getMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/deals")
    public ResponseEntity<?> showAllDeals() {
        List<LotTransactions> lotDealsByLots = lotTransactionsRepository.findAll();

        var responseBody = new DealsResponse(lotDealsByLots);
        return ResponseEntity.ok(responseBody);
    }

    @GetMapping("/lots")
    public ResponseEntity<?> showActiveLots() {
        List<LotOrder> lotOrders = lotOrdersRepository.findByIsActiveTrue();
        List<LotOffer> lotOffers = lotOffersRepository.findByIsActiveTrue();

        var responseBody = new ActiveLotsResponse(lotOrders, lotOffers);
        return ResponseEntity.ok(responseBody);
    }
}
