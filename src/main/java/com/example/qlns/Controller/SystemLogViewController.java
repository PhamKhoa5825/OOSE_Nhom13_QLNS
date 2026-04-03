package com.example.qlns.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SystemLogViewController {

    @GetMapping("/system-logs")
    public String systemLogs(Model model) {
        model.addAttribute("activePage", "system-logs");
        return "system-logs";
    }

    @GetMapping("/settings")
    public String settings(Model model) {
        model.addAttribute("activePage", "settings");
        return "settings";
    }
}