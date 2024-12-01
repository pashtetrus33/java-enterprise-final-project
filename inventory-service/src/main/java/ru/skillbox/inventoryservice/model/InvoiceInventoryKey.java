package ru.skillbox.inventoryservice.model;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Data
public class InvoiceInventoryKey implements Serializable {

    @Column(name = "invoice_id")
    private Long invoiceId;

    @Column(name = "inventory_id")
    private Long inventoryId;
}