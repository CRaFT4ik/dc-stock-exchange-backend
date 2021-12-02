package ru.er_log.stock.exchange.pojos.entities;

public class User {
    public final String name;

    public User(ru.er_log.stock.auth.models.User user) {
        this.name = user.getUsername();
    }
}
