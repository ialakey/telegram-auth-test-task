package com.alakey.telegram_auth_test_task;

import com.alakey.telegram_auth_test_task.service.TelegramValidationService;
import org.apache.commons.codec.digest.HmacUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

class TelegramValidationServiceTest {

    private TelegramValidationService validationService;

    private String botToken;


    @BeforeEach
    void setUp() throws IOException {
        Properties properties = new Properties();
        properties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        botToken = properties.getProperty("telegram.bot-token");

        validationService = new TelegramValidationService();
        ReflectionTestUtils.setField(validationService, "botToken", botToken);
    }

    @Test
    void testIsValid_withCorrectHash() throws Exception {
        Map<String, String> initData = new TreeMap<>();
        initData.put("auth_date", "1681924577");
        initData.put("id", "123456789");
        initData.put("first_name", "John");

        String dataString = String.join("\n", initData.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .toList());

        byte[] secretKey = new HmacUtils("HmacSHA256", "WebAppData").hmac(botToken);
        String hash = new HmacUtils("HmacSHA256", secretKey).hmacHex(dataString);

        String telegramInitData = initData.entrySet().stream()
                .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                .reduce((a, b) -> a + "&" + b)
                .orElse("") + "&hash=" + hash;

        boolean isValid = validationService.isValid(telegramInitData);
        assertTrue(isValid);
    }

    @Test
    void testIsValid_withIncorrectHash() throws Exception {
        String invalidInitData = "id=123456789&first_name=John&auth_date=1681924577&hash=invalidhash";
        boolean isValid = validationService.isValid(invalidInitData);
        assertFalse(isValid);
    }
}
