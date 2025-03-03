package org.example.atm_v2.Model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType transactionType;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();

    @Column(precision = 5, scale = 2)
    private BigDecimal interestRate; // Lãi suất

    @Column
    private Integer months;

    public Transaction(Account account, TransactionType transactionType, BigDecimal amount, BigDecimal interestRate, Integer months) {
        this.account = account;
        this.transactionType = transactionType;
        this.amount = amount;
        this.interestRate = interestRate;
        this.months = months;
        this.transactionDate = LocalDateTime.now();
    }
}
