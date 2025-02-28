package org.example.atm_v2.Respository;

import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Model.Transaction;
import org.example.atm_v2.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountId(Long accountId);

}
