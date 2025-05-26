package com.alakey.telegram_auth_test_task;

import com.alakey.telegram_auth_test_task.controller.TelegramController;
import com.alakey.telegram_auth_test_task.model.UserTelegram;
import com.alakey.telegram_auth_test_task.repository.UserRepository;
import com.alakey.telegram_auth_test_task.security.SecurityConfig;
import com.alakey.telegram_auth_test_task.service.TelegramValidationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TelegramController.class)
@Import(SecurityConfig.class)
class TelegramControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TelegramValidationService telegramValidationService;

    @MockBean
    private UserRepository userRepository;

    @Test
    void testAuthenticate_validData() throws Exception {
        String userJson = new ObjectMapper().writeValueAsString(Map.of(
                "id", 123456,
                "first_name", "John",
                "last_name", "Doe",
                "username", "johndoe"
        ));

        String encodedUserJson = URLEncoder.encode(userJson, StandardCharsets.UTF_8);
        String initData = "user=" + encodedUserJson;

        Mockito.when(telegramValidationService.isValid(initData)).thenReturn(true);
        Mockito.when(telegramValidationService.parseQueryString(initData)).thenReturn(Map.of("user", encodedUserJson));
        Mockito.when(userRepository.findById(123456L)).thenReturn(Optional.empty());

        UserTelegram user = new UserTelegram();
        user.setId(123456L);
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setUsername("johndoe");

        Mockito.when(userRepository.save(any(UserTelegram.class))).thenReturn(user);

        mockMvc.perform(post("/auth").param("initData", initData))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("user"))
                .andExpect(model().attribute("user", user))
                .andExpect(view().name("index :: #content"));
    }

    @Test
    void testAuthenticate_invalidData() throws Exception {
        String initData = "user=invalid";
        Mockito.when(telegramValidationService.isValid(initData)).thenReturn(false);

        mockMvc.perform(post("/auth").param("initData", initData))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", "Невалидные данные Telegram"))
                .andExpect(view().name("index :: #content"));
    }

    @Test
    void testAuthenticate_withException() throws Exception {
        String initData = "user=broken";

        Mockito.when(telegramValidationService.isValid(initData)).thenReturn(true);
        Mockito.when(telegramValidationService.parseQueryString(initData)).thenThrow(new RuntimeException("Parsing error"));

        mockMvc.perform(post("/auth").param("initData", initData))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("error"))
                .andExpect(model().attribute("error", org.hamcrest.Matchers.containsString("Ошибка при обработке данных Telegram")))
                .andExpect(view().name("index :: #content"));
    }
}
