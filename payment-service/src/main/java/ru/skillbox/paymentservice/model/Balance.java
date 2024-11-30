package ru.skillbox.paymentservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "balances")
@NoArgsConstructor
@Getter
@Setter
public class Balance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long userId;
    private Long balance;

    @JsonIgnore
    @OneToMany(
            mappedBy = "balance",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<PaymentDetails> paymentDetailsList = new ArrayList<>();
}