package ru.skillbox.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import ru.skillbox.orderservice.dto.enums.OrderStatus;
import ru.skillbox.orderservice.dto.enums.ServiceName;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "orders")
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "description")
    private String description;

    @Column(name = "departure_address")
    private String departureAddress;

    @Column(name = "destination_address")
    private String destinationAddress;

    @Column(name = "cost")
    private Long cost;

    @Column(name = "quantity")
    private Integer quantity;

    @JsonIgnore
    private Long userId;

    @CreationTimestamp
    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @UpdateTimestamp
    @Column(name = "modified_time")
    private LocalDateTime modifiedTime;

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(
            mappedBy = "order",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<OrderStatusHistory> orderStatusHistory = new ArrayList<>();

    public Order(
            String departureAddress,
            String destinationAddress,
            String description,
            Long cost,
            Long userId,
            Integer quantity,
            OrderStatus status
    ) {
        this.departureAddress = departureAddress;
        this.destinationAddress = destinationAddress;
        this.description = description;
        this.cost = cost;
        this.userId = userId;
        this.quantity = quantity;
        this.status = status;
    }

    public void addStatusHistory(OrderStatus status, ServiceName serviceName, String comment) {
        getOrderStatusHistory().add(new OrderStatusHistory(status, serviceName, comment, this));
    }
}