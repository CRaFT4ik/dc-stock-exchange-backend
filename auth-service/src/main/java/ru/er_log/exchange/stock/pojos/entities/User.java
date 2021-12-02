package ru.er_log.exchange.stock.pojos.entities;

public class User {
    public final String name;

    public User(ru.er_log.exchange.auth.models.User user) {
        this.name = user.getUsername();
    }
}
