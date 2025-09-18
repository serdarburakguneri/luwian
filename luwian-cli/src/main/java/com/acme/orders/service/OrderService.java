package com.acme.orders.service;

public class OrderService {
    public Object handle() {
        return new com.acme.orders.web.dto.HelloResponse("ok");
    }
}
