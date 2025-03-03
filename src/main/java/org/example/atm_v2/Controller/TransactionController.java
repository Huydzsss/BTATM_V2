package org.example.atm_v2.Controller;

import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Model.Transaction;
import org.example.atm_v2.Model.TransactionType;
import org.example.atm_v2.Respository.AccountRepository;
import org.example.atm_v2.Service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Controller
@RequestMapping("/transactions")
public class TransactionController {
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private AccountRepository accountRepository;
    @GetMapping
    public String listAllTransactions(Model model) {
        List<Transaction> transactions = transactionService.getAllTransactions();
        model.addAttribute("transactions", transactions);
        return "transactions";
    }

    @GetMapping("/{accountId}")
    public String getTransactions(@PathVariable Long accountId, Model model) {
        List<Transaction> transactions = transactionService.getTransactionsByAccount(accountId);
        model.addAttribute("transactions", transactions);
        model.addAttribute("accountId", accountId);
        return "transactions";
    }

    @PostMapping("/withdraw")
    public String withdraw(@RequestParam Long accountId, @RequestParam BigDecimal amount, Model model) {
        try {
            transactionService.createTransaction(accountId, amount, TransactionType.WITHDRAWAL);
        } catch (Exception e) {
            model.addAttribute("error", "Rút tiền thất bại: " + e.getMessage());
            return "error";
        }
        return "redirect:/transactions/" + accountId;
    }

    @PostMapping("/deposit")
    public String deposit(@RequestParam Long accountId, @RequestParam BigDecimal amount, Model model) {
        try {
            transactionService.createTransaction(accountId, amount, TransactionType.DEPOSIT);
        } catch (Exception e) {
            model.addAttribute("error", "Nạp tiền thất bại: " + e.getMessage());
            return "error";
        }
        return "redirect:/transactions/" + accountId;
    }

    @GetMapping("/deposit-form")
    public String showDepositForm(@RequestParam Long accountId, Model model) {
        model.addAttribute("accountId", accountId);
        return "deposit-form";
    }

    @GetMapping("/withdraw-form")
    public String showWithdrawForm(@RequestParam Long accountId, Model model) {
        model.addAttribute("accountId", accountId);
        return "withdraw-form";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccountId,
                           @RequestParam String toAccountNumber,
                           @RequestParam BigDecimal amount,
                           Model model) {
        try {
            System.out.println("fromAccountId: " + fromAccountId);
            System.out.println("toAccountNumber: " + toAccountNumber);
            System.out.println("amount: " + amount);

            Long toAccountId = accountRepository.findByAccountNumber(toAccountNumber)
                    .map(Account::getId)
                    .orElse(null);

            transactionService.transferMoney(fromAccountId, toAccountNumber, amount);

            // Redirect đến transactions của toAccountId nếu tìm thấy
            return toAccountId != null ? "redirect:/transactions/" + toAccountId : "redirect:/transactions";
        } catch (Exception e) {
            model.addAttribute("error", "Chuyển khoản thất bại: " + e.getMessage());
            return "error";
        }
    }
    @PostMapping("/saving")
    public String createSavingAccount(@RequestParam Long accountId,
                                      @RequestParam BigDecimal amount,
                                      @RequestParam BigDecimal interestRate,
                                      @RequestParam Integer months,
                                      Model model) {
        try {
            transactionService.createSavingTransaction(accountId, amount, interestRate, months);
        } catch (Exception e) {
            model.addAttribute("error", "Gửi tiết kiệm thất bại: " + e.getMessage());
            return "error";
        }
        return "redirect:/transactions/" + accountId;
    }


}
