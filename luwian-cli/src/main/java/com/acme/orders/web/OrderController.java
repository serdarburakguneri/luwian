package com.acme.orders.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/orders", produces = "application/json")
public class OrderController {

    private final OrderService service;

    public OrderController(OrderService service) {
        this.service = service;
    }

    @GET
    public ResponseEntity<?> handle() {
        return ResponseEntity.status(200).body(service.handle());
    }
}
