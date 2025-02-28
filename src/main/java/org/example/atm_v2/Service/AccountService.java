package org.example.atm_v2.Service;

import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Model.Transaction;
import org.example.atm_v2.Model.TransactionType;
import org.example.atm_v2.Respository.AccountRepository;
import org.example.atm_v2.Respository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Optional<Account> getAccountById(Long accountId) {
        return accountRepository.findById(accountId);
    }

    public Optional<Account> getAccountByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber);
    }

    @Transactional
    public void transfer(Long fromAccountId, Long toAccountId, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản gửi không tồn tại"));
        Account toAccount = accountRepository.findById(toAccountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản nhận không tồn tại"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để chuyển tiền!");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        accountRepository.save(fromAccount);
        accountRepository.save(toAccount);

        Transaction transaction = new Transaction();
        transaction.setAccount(fromAccount);
        transaction.setTransactionType(TransactionType.TRANSFER_IN);
        transaction.setAmount(amount);
        transactionRepository.save(transaction);
    }
}
