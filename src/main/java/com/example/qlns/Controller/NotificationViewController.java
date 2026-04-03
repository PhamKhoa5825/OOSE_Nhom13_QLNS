package com.example.qlns.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NotificationViewController {

    @GetMapping("/notifications")
    public String notifications(Model model) {
        model.addAttribute("activePage", "notifications");
        return "notifications";
    }
}
