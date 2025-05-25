package com.alakey.telegram_auth_test_task.controller;

import com.alakey.telegram_auth_test_task.model.UserTelegram;
import com.alakey.telegram_auth_test_task.repository.UserRepository;
import com.alakey.telegram_auth_test_task.service.TelegramValidationService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Controller
public class TelegramController {

    private final TelegramValidationService telegramValidationService;
    private final UserRepository userRepository;

    public TelegramController(TelegramValidationService telegramValidationService, UserRepository userRepository) {
        this.telegramValidationService = telegramValidationService;
        this.userRepository = userRepository;
    }

    @PostMapping("/auth")
    public String authenticate(@RequestParam String initData, Model model) {
        try {
            if (!telegramValidationService.isValid(initData)) {
                model.addAttribute("error", "Невалидные данные Telegram");
                return "index :: #content";
            }

            UserTelegram user = parseAndSaveUser(initData);
            model.addAttribute("user", user);
        } catch (Exception e) {
            model.addAttribute("error", "Ошибка при обработке данных Telegram: " + e.getMessage());
        }

        return "index :: #content";
    }

    private UserTelegram parseAndSaveUser(String initData) throws Exception {
        Map<String, String> params = telegramValidationService.parseQueryString(initData);
        String userJsonEncoded = params.get("user");

        if (userJsonEncoded == null) {
            throw new IllegalArgumentException("Нет параметра 'user' в данных Telegram");
        }

        String userJson = URLDecoder.decode(userJsonEncoded, StandardCharsets.UTF_8);

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> userMap = objectMapper.readValue(userJson, new TypeReference<>() {});

        Long id = ((Number) userMap.get("id")).longValue();

        UserTelegram user = userRepository.findById(id).orElse(new UserTelegram());
        user.setId(id);
        user.setFirstName((String) userMap.get("first_name"));
        user.setLastName((String) userMap.get("last_name"));
        user.setUsername((String) userMap.get("username"));

        return userRepository.save(user);
    }

}
