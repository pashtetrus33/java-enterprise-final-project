package ru.skillbox.inventoryservice.model;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "invoices")
@Setter
@Getter
public class Invoice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;

    @CreationTimestamp
    private LocalDateTime creationDate;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.REMOVE)
    Set<InvoiceInventory> invoiceInventorySet = new HashSet<>();
}