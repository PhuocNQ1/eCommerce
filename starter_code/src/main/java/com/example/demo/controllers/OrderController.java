package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private static final Logger log = Logger.getLogger(OrderController.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;


    @PostMapping("/submit/{username}")
    public ResponseEntity<UserOrder> submit(@PathVariable String username) {
        log.info("Received order submission request for user: {" + username + "}");

        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User with username '{" + username + "}' not found. Order submission failed.");
            return ResponseEntity.notFound().build();
        }

        UserOrder order = UserOrder.createFromCart(user.getCart());
        orderRepository.save(order);

        log.info("Order submitted successfully for user: {" + username + "}, Order ID: {" + order.getId() + "}");

        return ResponseEntity.ok(order);
    }

    @GetMapping("/history/{username}")
    public ResponseEntity<List<UserOrder>> getOrdersForUser(@PathVariable String username) {
        log.info("Retrieving order history for user: {" + username + "}");

        User user = userRepository.findByUsername(username);
        if (user == null) {
            log.warn("User with username '{" + username + "}' not found. Unable to retrieve order history.");
            return ResponseEntity.notFound().build();
        }

        List<UserOrder> orderHistory = orderRepository.findByUser(user);

        log.info("Order history retrieved successfully for user: {" + username + "}");

        return ResponseEntity.ok(orderHistory);
    }
}
