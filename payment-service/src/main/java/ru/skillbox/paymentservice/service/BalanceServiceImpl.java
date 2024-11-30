package ru.skillbox.paymentservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.skillbox.paymentservice.dto.SumDto;
import ru.skillbox.paymentservice.exception.BalanceExistsException;
import ru.skillbox.paymentservice.exception.BalanceNotFoundException;
import ru.skillbox.paymentservice.model.Balance;
import ru.skillbox.paymentservice.repository.BalanceRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private final BalanceRepository balanceRepository;


    @Override
    public Balance createBalance(Long userId) throws BalanceExistsException {
        Optional<Balance> optionalBalance = balanceRepository.findBalanceByUserId(userId);
        if (optionalBalance.isPresent()) {
            throw new BalanceExistsException("A balance account for the specified user already exists.");
        }

        Balance balance = new Balance();
        balance.setUserId(userId);
        balance.setBalance(0L);
        return balanceRepository.save(balance);
    }

    @Override
    public void replenishBalance(Long balanceId, SumDto sumDto) throws BalanceNotFoundException {
        Optional<Balance> optionalBalance = balanceRepository.findById(balanceId);
        if (optionalBalance.isEmpty()) {
            throw new BalanceNotFoundException("The balance with the specified ID was not found.");
        }

        Balance balance = optionalBalance.get();
        balance.setBalance(balance.getBalance() + sumDto.getSum());
        balanceRepository.save(balance);
    }
}