package ru.skillbox.orderservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.orderservice.model.Order;
import ru.skillbox.orderservice.dto.OrderDto;
import ru.skillbox.orderservice.dto.StatusDto;
import ru.skillbox.orderservice.service.OrderService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;


    @Operation(summary = "Create a new order.", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/order")
    public ResponseEntity<?> addOrder(@Valid @RequestBody OrderDto input, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.addOrder(input, request));
    }

    @Operation(summary = "Update order status by id.", security = @SecurityRequirement(name = "bearerAuth"))
    @PatchMapping("/order/{orderId}")
    public ResponseEntity<Void> updateOrderStatus(@PathVariable @Parameter(description = "Id of order") long orderId,
                                                  @RequestBody StatusDto statusDto) throws OrderNotFoundException {

        orderService.updateOrderStatus(orderId, statusDto);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "List all orders in delivery system", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/order")
    public List<Order> listOrders() {
        return orderService.findAll();
    }

    @Operation(summary = "Get an order in system by id", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/order/{orderId}")
    public Order listOrder(@PathVariable @Parameter(description = "Id of order") Long orderId) {
        return orderService.findById(orderId);
    }
}