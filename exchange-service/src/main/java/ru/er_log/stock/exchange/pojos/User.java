package ru.er_log.stock.exchange.pojos;

public class User {
    public final String name;

    public User(ru.er_log.stock.auth.enities.User user) {
        this.name = user.getUsername();
    }
}
