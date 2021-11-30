package ru.er_log.exchange.cart.controllers;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.er_log.exchange.cart.models.Cart;
import ru.er_log.exchange.cart.models.Product;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("v1/cart")
public class CartController {

    @GetMapping("/{userId}")
    public Cart getCart(@Validated @PathVariable long userId) {
        // For simplicity we are returning a hard coded value
        List<Product> products = new ArrayList<>();
        //p1
        Product p1 = new Product(1, "keyboard", 250, 2);
        p1.setTotlalPrice(p1.getBasePrice() * p1.getQuantity());
        products.add(p1);

        //p2
        Product p2 = new Product(2, "mouse", 150, 2);
        p2.setTotlalPrice(p2.getBasePrice() * p2.getQuantity());
        products.add(p2);

        //calculating total price
        double totalPrice = products.stream().mapToDouble(Product::getTotlalPrice).sum();

        return new Cart(products.size(), totalPrice, products);
    }
}