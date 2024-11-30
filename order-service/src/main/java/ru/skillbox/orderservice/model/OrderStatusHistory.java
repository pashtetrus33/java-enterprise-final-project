package ru.skillbox.orderservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.skillbox.orderservice.model.enums.OrderStatus;
import ru.skillbox.orderservice.model.enums.ServiceName;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Stores the history of order status changes
 */
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "order_status_history")
public class OrderStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column(name = "service_name")
    @Enumerated(EnumType.STRING)
    private ServiceName serviceName;

    @Column(name = "comment")
    private String comment;

    @CreationTimestamp
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    public OrderStatusHistory(OrderStatus status, ServiceName serviceName, String comment, Order order) {
        this.status = status;
        this.serviceName = serviceName;
        this.comment = comment;
        this.order = order;
    }
}