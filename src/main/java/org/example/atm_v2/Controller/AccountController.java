package org.example.atm_v2.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.atm_v2.Model.Account;
import org.example.atm_v2.Model.User;
import org.example.atm_v2.Respository.AccountRepository;
import org.example.atm_v2.Service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/accounts")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

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
    @PostMapping("/create")
    public String createAccount(@RequestParam String accountType,
                                @RequestParam BigDecimal balance,
                                HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login?error=unauthorized";
        }

        // Kiểm tra nếu accountType rỗng
        if (accountType == null || accountType.trim().isEmpty()) {
            model.addAttribute("error", "Loại tài khoản không được để trống!");
            return "create-account"; // Trang hiển thị lỗi
        }

        // Tạo số tài khoản ngẫu nhiên (có thể sửa thành thuật toán khác nếu cần)
        String accountNumber = generateRandomAccountNumber();

        // Tạo tài khoản mới
        Account newAccount = new Account();
        newAccount.setAccountNumber(accountNumber);
        newAccount.setUser(user);
        newAccount.setBalance(balance);
        newAccount.setAccountType(accountType); // 🔥 Đảm bảo gán giá trị

        // Lưu vào database
        accountService.saveAccount(newAccount);

        model.addAttribute("message", "Tạo tài khoản thành công!");
        return "dashboard"; // Điều hướng về trang chính
    }

    // Hàm tạo số tài khoản ngẫu nhiên
    private String generateRandomAccountNumber() {
        return "AC" + new Random().nextInt(99999999);
    }




}
