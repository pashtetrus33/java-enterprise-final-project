package ru.skillbox.inventoryservice.service;

import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.inventoryservice.dto.*;
import ru.skillbox.inventoryservice.dto.enums.OrderStatus;
import ru.skillbox.inventoryservice.dto.enums.ServiceName;
import ru.skillbox.inventoryservice.exception.InventoryNotFoundException;
import ru.skillbox.inventoryservice.exception.NotEnoughInventoryException;
import ru.skillbox.inventoryservice.model.Inventory;
import ru.skillbox.inventoryservice.model.Invoice;
import ru.skillbox.inventoryservice.model.InvoiceInventory;
import ru.skillbox.inventoryservice.model.InvoiceInventoryKey;
import ru.skillbox.inventoryservice.repository.InventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceInventoryRepository;
import ru.skillbox.inventoryservice.repository.InvoiceRepository;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final InvoiceRepository invoiceRepository;
    private final InvoiceInventoryRepository invoiceInventoryRepository;
    private final RequestSendingService requestSendingService;
    private final KafkaService kafkaService;


    @Transactional
    @Override
    public void completeOrderInventory(InventoryKafkaDto inventoryKafkaDto) {
        try {
            Thread.sleep(3000);
            Long orderId = inventoryKafkaDto.getOrderId();
            String authHeaderValue = inventoryKafkaDto.getAuthHeaderValue();

            Invoice invoice = new Invoice();
            invoice.setOrderId(orderId);
            Invoice savedInvoice = invoiceRepository.save(invoice);

            shipInventoryForOrder(inventoryKafkaDto, savedInvoice);

            String comment = "The order has been completed successfully.";
            StatusDto statusDto = createStatusDto(OrderStatus.INVENTED, comment);
            DeliveryKafkaDto deliveryKafkaDto = createDeliveryKafkaDto(savedInvoice.getId(), inventoryKafkaDto);
            sendData(orderId, statusDto, authHeaderValue, deliveryKafkaDto);

        } catch (Exception ex) {
            if (!(ex instanceof InventoryNotFoundException) && !(ex instanceof NotEnoughInventoryException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(createErrorKafkaDto(inventoryKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void returnInventory(ErrorKafkaDto errorKafkaDto) {
        try {
            Long orderId = errorKafkaDto.getOrderId();
            List<InvoiceInventory> invoiceInventories
                    = invoiceInventoryRepository.findByInvoice_OrderId(orderId);

            List<Inventory> updatedInventoryList = new ArrayList<>();
            invoiceInventories.forEach(invoiceInventory -> {
                Inventory inventory = invoiceInventory.getInventory();
                inventory.setQuantity(inventory.getQuantity() + invoiceInventory.getQuantity());
                updatedInventoryList.add(inventory);
            });

            inventoryRepository.saveAll(updatedInventoryList);
            invoiceRepository.deleteByOrderId(orderId);

            kafkaService.produce(errorKafkaDto);

        } catch (Exception ex) {
            StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
            kafkaService.produce(createErrorKafkaDto(errorKafkaDto.getOrderId(), statusDto));

            throw new RuntimeException(ex.getMessage());
        }
    }

    @Override
    public void replenishInventory(long inventoryId, QuantityDto quantityDto) throws InventoryNotFoundException {
        Optional<Inventory> optionalInventory = inventoryRepository.findById(inventoryId);
        if (optionalInventory.isEmpty()) {
            throw new InventoryNotFoundException("Inventory with the specified ID " + inventoryId + " was not found.");
        }

        Inventory inventory = optionalInventory.get();
        inventory.setQuantity(inventory.getQuantity() + quantityDto.getQuantity());
        inventoryRepository.save(inventory);
    }

    @Override
    public Inventory createInventory(InventoryDto inventoryDto, Long userId) {
        Inventory inventory = new Inventory();
        inventory.setDescription(inventoryDto.getDescription());
        inventory.setCostPerItem(inventoryDto.getCostPerItem());
        inventory.setQuantity(inventoryDto.getQuantity());

        return inventoryRepository.save(inventory);
    }

    private DeliveryKafkaDto createDeliveryKafkaDto(Long invoiceId, InventoryKafkaDto inventoryKafkaDto) {
        DeliveryKafkaDto deliveryKafkaDto = new DeliveryKafkaDto();
        deliveryKafkaDto.setUserId(inventoryKafkaDto.getUserId());
        deliveryKafkaDto.setOrderId(inventoryKafkaDto.getOrderId());
        deliveryKafkaDto.setInvoiceId(invoiceId);
        deliveryKafkaDto.setOrderDto(inventoryKafkaDto.getOrderDto());
        deliveryKafkaDto.setAuthHeaderValue(inventoryKafkaDto.getAuthHeaderValue());

        return deliveryKafkaDto;
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.INVENTORY_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }

    private ErrorKafkaDto createErrorKafkaDto(Long orderId, StatusDto statusDto) {
        ErrorKafkaDto errorPaymentKafkaDto = new ErrorKafkaDto();
        errorPaymentKafkaDto.setOrderId(orderId);
        errorPaymentKafkaDto.setStatusDto(statusDto);

        return errorPaymentKafkaDto;
    }

    private void recordChangesInTheInvoice(Inventory inventory, Invoice invoice, Integer quantity) {
        InvoiceInventoryKey invoiceInventoryKey = new InvoiceInventoryKey();
        invoiceInventoryKey.setInventoryId(inventory.getId());
        invoiceInventoryKey.setInvoiceId(invoice.getId());

        InvoiceInventory invoiceInventory = new InvoiceInventory();
        invoiceInventory.setInvoiceInventoryKey(invoiceInventoryKey);
        invoiceInventory.setInventory(inventory);
        invoiceInventory.setInvoice(invoice);
        invoiceInventory.setQuantity(quantity);
        invoiceInventoryRepository.save(invoiceInventory);
    }

    private void sendData(Long orderId, StatusDto statusDto, String authHeaderValue, Object kafkaDto) {
        requestSendingService.updateOrderStatusInOrderService(orderId, statusDto, authHeaderValue);
        kafkaService.produce(kafkaDto);
    }

    private void shipInventoryForOrder(InventoryKafkaDto kafkaDto, Invoice invoice) throws InventoryNotFoundException {
        Long orderId = kafkaDto.getOrderId();
        String authHeaderValue = kafkaDto.getAuthHeaderValue();
        String description = kafkaDto.getOrderDto().getDescription();
        Optional<Inventory> byDescription = inventoryRepository.findByDescription(description);

        if (byDescription.isEmpty()) {
            String comment = "Inventory with description " + description + " was not found.";
            StatusDto statusDto = createStatusDto(OrderStatus.INVENTMENT_FAILED, comment);
            ErrorKafkaDto errorKafkaDto = createErrorKafkaDto(orderId, statusDto);
            sendData(orderId, statusDto, authHeaderValue, errorKafkaDto);

            throw new InventoryNotFoundException(comment);
        }

        Integer countProductFromDto = kafkaDto.getOrderDto().getQuantity();
        Inventory inventory = byDescription.get();
        Integer countProductFromDatabase = inventory.getQuantity();
        if (countProductFromDatabase < countProductFromDto) {
            String comment = "Insufficient inventory with description " + description;
            StatusDto statusDto = createStatusDto(OrderStatus.INVENTMENT_FAILED, comment);
            ErrorKafkaDto errorKafkaDto = createErrorKafkaDto(orderId, statusDto);
            sendData(orderId, statusDto, authHeaderValue, errorKafkaDto);

            throw new NotEnoughInventoryException(comment);
        }

        countProductFromDatabase = countProductFromDatabase - countProductFromDto;
        inventory.setQuantity(countProductFromDatabase);
        inventoryRepository.save(inventory);

        recordChangesInTheInvoice(inventory, invoice, countProductFromDto);
    }
}