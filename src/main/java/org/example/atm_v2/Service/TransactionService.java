package org.example.atm_v2.Service;

import jakarta.transaction.Transactional;
import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Model.Transaction;
import org.example.atm_v2.Model.TransactionType;
import org.example.atm_v2.Respository.AccountRepository;
import org.example.atm_v2.Respository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    public List<Transaction> getTransactionsByAccount(Long accountId) {
        return transactionRepository.findByAccountId(accountId);
    }

    public Transaction createTransaction(Long accountId, BigDecimal amount, TransactionType type) {
        Account account = accountRepository.findById(accountId).orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));

        if (type == TransactionType.WITHDRAWAL && account.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để rút tiền!");
        }

        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(type);
        transaction.setTransactionDate(LocalDateTime.now());

        if (type == TransactionType.WITHDRAWAL) {
            account.setBalance(account.getBalance().subtract(amount));
        } else if (type == TransactionType.DEPOSIT) {
            account.setBalance(account.getBalance().add(amount));
        }

        accountRepository.save(account);
        return transactionRepository.save(transaction);
    }
    @Transactional
    public void transferMoney(Long fromAccountId, String toAccountNumber, BigDecimal amount) {
        Account fromAccount = accountRepository.findById(fromAccountId)
                .orElseThrow(() -> new RuntimeException("Tài khoản nguồn không tồn tại!"));

        Account toAccount = accountRepository.findByAccountNumber(toAccountNumber)
                .orElseThrow(() -> new RuntimeException("Tài khoản đích không tồn tại!"));

        if (fromAccount.getAccountNumber().equals(toAccountNumber)) {
            throw new RuntimeException("Không thể chuyển tiền đến chính tài khoản của bạn!");
        }

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new RuntimeException("Số dư không đủ để chuyển tiền!");
        }


        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        accountRepository.save(fromAccount);

        toAccount.setBalance(toAccount.getBalance().add(amount));
        accountRepository.save(toAccount);

        Transaction withdrawTransaction = new Transaction();
        withdrawTransaction.setAccount(fromAccount);
        withdrawTransaction.setAmount(amount);
        withdrawTransaction.setTransactionType(TransactionType.TRANSFER_OUT);
        withdrawTransaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(withdrawTransaction);

        Transaction depositTransaction = new Transaction();
        depositTransaction.setAccount(toAccount);
        depositTransaction.setAmount(amount);
        depositTransaction.setTransactionType(TransactionType.TRANSFER_IN);
        depositTransaction.setTransactionDate(LocalDateTime.now());
        transactionRepository.save(depositTransaction);
    }
    public void createSavingTransaction(Long accountId, BigDecimal amount, BigDecimal interestRate, Integer months) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new IllegalArgumentException("Tài khoản không tồn tại"));

        // Logic xử lý gửi tiết kiệm (có thể thêm tính toán lãi suất)
        Transaction transaction = new Transaction();
        transaction.setAccount(account);
        transaction.setAmount(amount);
        transaction.setTransactionType(TransactionType.SAVING);
        transaction.setInterestRate(interestRate);
        transaction.setMonths(months);

        transactionRepository.save(transaction);
    }

}

