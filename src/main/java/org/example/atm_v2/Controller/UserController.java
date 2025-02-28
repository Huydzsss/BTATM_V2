package org.example.atm_v2.Controller;

import jakarta.servlet.http.HttpSession;
import org.example.atm_v2.Model.User;
import org.example.atm_v2.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String error, Model model) {
        if (error != null) {
            model.addAttribute("error", "Sai tài khoản hoặc mật khẩu!");
        }
        return "login";
    }

    @PostMapping("/login")
    public String handleLogin(@RequestParam String username, @RequestParam String password, HttpSession session) {
        User user = userService.findByUsername(username);

        if (user == null || !userService.checkPassword(password, user.getPassword())) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("user", user);
        return "redirect:/dashboard";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String handleRegister(@RequestParam String username,
                                 @RequestParam String password,
                                 @RequestParam String fullName,
                                 Model model) {
        if (userService.findByUsername(username) != null) {
            model.addAttribute("error", "Tên tài khoản đã tồn tại!");
            return "register";
        }

        User newUser = new User();
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setFullName(fullName);
        userService.registerUser(newUser); // Mã hóa mật khẩu và lưu vào database

        return "redirect:/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // Xóa session
        return "redirect:/login";
    }
}
