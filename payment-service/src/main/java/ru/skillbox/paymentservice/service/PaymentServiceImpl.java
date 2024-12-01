package ru.skillbox.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.skillbox.paymentservice.dto.*;
import ru.skillbox.paymentservice.dto.enums.OrderStatus;
import ru.skillbox.paymentservice.dto.enums.ServiceName;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;
import ru.skillbox.paymentservice.exception.InsufficientFundsException;
import ru.skillbox.paymentservice.exception.PaymentDetailsNotFoundException;
import ru.skillbox.paymentservice.model.Balance;
import ru.skillbox.paymentservice.model.PaymentDetails;
import ru.skillbox.paymentservice.repository.BalanceRepository;
import ru.skillbox.paymentservice.repository.PaymentDetailsRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final BalanceRepository balanceRepository;
    private final PaymentDetailsRepository paymentDetailsRepository;
    private final KafkaService kafkaService;
    private final RequestSendingService requestSendingService;


    @Transactional
    @Override
    public void pay(PaymentKafkaDto paymentKafkaDto) {
        try {
            Thread.sleep(3000);
            Long cost = paymentKafkaDto.getOrderDto().getCost();
            Long userId = paymentKafkaDto.getUserId();
            Long orderId = paymentKafkaDto.getOrderId();
            String authHeaderValue = paymentKafkaDto.getAuthHeaderValue();

            Optional<Balance> optionalUserBalance = balanceRepository.findBalanceByUserId(userId);
            if (optionalUserBalance.isEmpty()) {
                String comment = "User account not found.";
                StatusDto statusDto = createStatusDto(OrderStatus.PAYMENT_FAILED, comment);
                ErrorKafkaDto errorKafkaDto = createErrorKafkaDto(orderId, statusDto);
                sendData(orderId, statusDto, authHeaderValue, errorKafkaDto);

                throw new BalanceNotFoundException(comment);
            }

            Balance userBalance = optionalUserBalance.get();
            if (userBalance.getBalance() < cost) {
                String comment = "There are not enough funds to make the payment.";
                StatusDto statusDto = createStatusDto(OrderStatus.PAYMENT_FAILED, comment);
                ErrorKafkaDto errorKafkaDto = createErrorKafkaDto(orderId, statusDto);
                sendData(orderId, statusDto, authHeaderValue, errorKafkaDto);

                throw new InsufficientFundsException(comment);
            }

            recordFactOfPayment(cost, userBalance, paymentKafkaDto.getOrderId());

            String comment = "The account balance has been successfully changed.";
            StatusDto statusDto = createStatusDto(OrderStatus.PAID, comment);
            InventoryKafkaDto inventoryKafkaDto = createInventoryKafkaDto(paymentKafkaDto);
            sendData(orderId, statusDto, authHeaderValue, inventoryKafkaDto);

        } catch (Exception ex) {
            if (!(ex instanceof BalanceNotFoundException) && !(ex instanceof InsufficientFundsException)) {
                StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
                kafkaService.produce(createErrorKafkaDto(paymentKafkaDto.getOrderId(), statusDto));
            }

            throw new RuntimeException(ex.getMessage());
        }
    }

    @Transactional
    @Override
    public void resetPayment(ErrorKafkaDto errorKafkaDto) {
        try {
            Optional<PaymentDetails> optionalPaymentDetails
                    = paymentDetailsRepository.findByOrderId(errorKafkaDto.getOrderId());

            if (optionalPaymentDetails.isEmpty()) {
                String message = "Payment details for order ID " + errorKafkaDto.getOrderId() + " were not found.";
                throw new PaymentDetailsNotFoundException(message);
            }

            PaymentDetails paymentDetails = optionalPaymentDetails.get();
            Balance balance = paymentDetails.getBalance();
            balance.setBalance(balance.getBalance() + paymentDetails.getSum());
            balanceRepository.save(balance);
            paymentDetailsRepository.delete(paymentDetails);
            paymentDetailsRepository.flush();

            kafkaService.produce(errorKafkaDto);

        } catch (Exception ex) {
            StatusDto statusDto = createStatusDto(OrderStatus.UNEXPECTED_FAILURE, ex.getMessage());
            kafkaService.produce(createErrorKafkaDto(errorKafkaDto.getOrderId(), statusDto));

            throw new RuntimeException(ex.getMessage());
        }
    }

    private InventoryKafkaDto createInventoryKafkaDto(PaymentKafkaDto paymentKafkaDto) {
        InventoryKafkaDto inventoryKafkaDto = new InventoryKafkaDto();
        inventoryKafkaDto.setUserId(paymentKafkaDto.getUserId());
        inventoryKafkaDto.setOrderId(paymentKafkaDto.getOrderId());
        inventoryKafkaDto.setOrderDto(paymentKafkaDto.getOrderDto());
        inventoryKafkaDto.setAuthHeaderValue(paymentKafkaDto.getAuthHeaderValue());
        return inventoryKafkaDto;
    }

    private ErrorKafkaDto createErrorKafkaDto(Long orderId, StatusDto statusDto) {
        ErrorKafkaDto errorKafkaDto = new ErrorKafkaDto();
        errorKafkaDto.setOrderId(orderId);
        errorKafkaDto.setStatusDto(statusDto);
        return errorKafkaDto;
    }

    private void recordFactOfPayment(Long cost, Balance userBalance, Long orderId) {
        PaymentDetails paymentDetails = new PaymentDetails();
        paymentDetails.setSum(cost);
        paymentDetails.setBalance(userBalance);
        paymentDetails.setOrderId(orderId);
        paymentDetailsRepository.save(paymentDetails);

        userBalance.setBalance(userBalance.getBalance() - cost);
        balanceRepository.save(userBalance);
    }

    private StatusDto createStatusDto(OrderStatus orderStatus, String comment) {
        StatusDto statusDto = new StatusDto();
        statusDto.setStatus(orderStatus);
        statusDto.setServiceName(ServiceName.PAYMENT_SERVICE);
        statusDto.setComment(comment);

        return statusDto;
    }

    private void sendData(Long orderId, StatusDto statusDto, String authHeaderValue, Object kafkaDto) {
        requestSendingService.updateOrderStatusInOrderService(orderId, statusDto, authHeaderValue);
        kafkaService.produce(kafkaDto);
    }
}