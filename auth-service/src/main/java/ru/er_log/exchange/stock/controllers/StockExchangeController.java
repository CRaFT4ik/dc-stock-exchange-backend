package ru.er_log.exchange.stock.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.er_log.exchange.auth.repository.UserRepository;
import ru.er_log.exchange.auth.service.UserDetailsImpl;
import ru.er_log.exchange.auth.service.UserDetailsServiceImpl;
import ru.er_log.exchange.stock.models.LotPurchase;
import ru.er_log.exchange.stock.pojo.LotDealRequest;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/exchange")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StockExchangeController {

    @Autowired
    UserDetailsServiceImpl userDetailsService;

    @PostMapping("/buy")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> addLotBuy(HttpServletRequest request, @RequestBody LotDealRequest dealRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        //UserDetails userDetails = userDetailsService.loadUserByUsername(userDetails.getUsername());

        new LotPurchase(dealRequest.getPrice(), userDetails.getId());

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/sell")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> addLotSell(@RequestBody LotDealRequest dealRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/deals")
    @PreAuthorize("isFullyAuthenticated()")
    public ResponseEntity<?> showDeals() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }

    @GetMapping("/lots")
    public ResponseEntity<?> showLots() {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).build();
    }
}
