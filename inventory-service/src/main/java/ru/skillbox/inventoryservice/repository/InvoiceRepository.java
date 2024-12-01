package ru.skillbox.inventoryservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.skillbox.inventoryservice.model.Invoice;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    void deleteByOrderId(Long orderId);
}