package org.example.atm_v2.Controller;

import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/{accountId}")
    public String getAccount(@PathVariable Long accountId, Model model) {
        Account account = accountService.getAccountById(accountId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy tài khoản!"));
        model.addAttribute("account", account);
        return "account";
    }

    @PostMapping("/transfer")
    public String transfer(@RequestParam Long fromAccountId, @RequestParam Long toAccountId, @RequestParam BigDecimal amount) {
        accountService.transfer(fromAccountId, toAccountId, amount);
        return "redirect:/accounts/" + fromAccountId;
    }
}
