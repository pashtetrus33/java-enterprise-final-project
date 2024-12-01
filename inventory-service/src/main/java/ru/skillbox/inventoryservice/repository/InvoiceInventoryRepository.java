package ru.skillbox.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.inventoryservice.model.InvoiceInventory;
import ru.skillbox.inventoryservice.model.InvoiceInventoryKey;

import java.util.List;

@Repository
public interface InvoiceInventoryRepository extends JpaRepository<InvoiceInventory, InvoiceInventoryKey> {

    List<InvoiceInventory> findByInvoice_OrderId(Long orderId);
}