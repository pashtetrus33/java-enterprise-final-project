package service;

import config.TestConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.skillbox.inventoryservice.dto.ErrorKafkaDto;
import ru.skillbox.inventoryservice.dto.InventoryKafkaDto;
import ru.skillbox.inventoryservice.dto.OrderDto;
import ru.skillbox.inventoryservice.dto.StatusDto;
import ru.skillbox.inventoryservice.dto.enums.OrderStatus;
import ru.skillbox.inventoryservice.dto.enums.ServiceName;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.model.Invoice;
import ru.skillbox.inventoryservice.model.InvoiceInventory;
import ru.skillbox.inventoryservice.model.InvoiceInventoryKey;
import ru.skillbox.inventoryservice.repository.InventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceInventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceRepository;
import ru.skillbox.inventoryservice.service.InventoryService;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@ContextConfiguration(classes = TestConfig.class)
class InventoryServiceTest {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private InvoiceInventoryRepository invoiceInventoryRepository;

    private InventoryKafkaDto inventoryKafkaDto;

    @BeforeEach
    void setUp() {
        OrderDto orderDto = new OrderDto(
                "product",
                "testDepartureAddress",
                "testDestinationAddress",
                100L,
                2
        );


        inventoryKafkaDto = new InventoryKafkaDto();
        inventoryKafkaDto.setOrderId(1L);
        inventoryKafkaDto.setUserId(1L);
        inventoryKafkaDto.setOrderDto(orderDto);
        inventoryKafkaDto.setAuthHeaderValue("test header value");
    }

    @AfterEach
    void tearDown() {
        inventoryKafkaDto = null;
    }

    @Test
    void completeOrderInventoryWithoutExceptionTest() {
        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(1L);

        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(30);
        inventory.setDescription("test title");
        inventory.setCostPerItem(2);

        when(invoiceRepository.save(Mockito.any(Invoice.class))).thenReturn(invoice);
        when(inventoryRepository.findByDescription(Mockito.any(String.class))).thenReturn(Optional.of(inventory));
        assertDoesNotThrow(() -> inventoryService.completeOrderInventory(inventoryKafkaDto));
    }

    @Test
    void completeOrderInventoryThrowExceptionTest() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(30);
        inventory.setDescription("test title");
        inventory.setCostPerItem(2);

        doThrow(new RuntimeException("Undefined test exception")).when(inventoryRepository).save(inventory);
        assertThrows(RuntimeException.class, () -> inventoryService.completeOrderInventory(inventoryKafkaDto));
    }

    @Test
    void returnInventoryThrowExceptionTest() {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(OrderStatus.DELIVERY_FAILED);
        statusDto.setServiceName(ServiceName.DELIVERY_SERVICE);
        statusDto.setComment("test comment.");

        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto();
        errorKafkaDto.setStatusDto(statusDto);
        errorKafkaDto.setOrderId(2L);

        doThrow(new RuntimeException("Undefined exception.")).when(invoiceInventoryRepository).findByInvoice_OrderId(2L);
        assertThrows(RuntimeException.class, () -> inventoryService.returnInventory(errorKafkaDto));
    }

    @Test
    void returnInventoryWithoutExceptionTest() {
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(30);
        inventory.setDescription("test title");
        inventory.setCostPerItem(2);

        Invoice invoice = new Invoice();
        invoice.setId(1L);
        invoice.setOrderId(1L);

        InvoiceInventoryKey invoiceInventoryKey = new InvoiceInventoryKey();
        invoiceInventoryKey.setInventoryId(1L);
        invoiceInventoryKey.setInvoiceId(1L);

        InvoiceInventory invoiceInventory = new InvoiceInventory();
        invoiceInventory.setInventory(inventory);
        invoiceInventory.setInvoice(invoice);
        invoiceInventory.setInvoiceInventoryKey(invoiceInventoryKey);

        when(invoiceInventoryRepository.findByInvoice_OrderId(1L)).thenReturn(List.of(invoiceInventory));
        assertThrows(RuntimeException.class, () -> inventoryService.completeOrderInventory(inventoryKafkaDto));
    }
}