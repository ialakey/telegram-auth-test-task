package com.alakey.telegram_auth_test_task.service;

import com.alakey.telegram_auth_test_task.util.Pair;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;


@Slf4j
@Service
public class TelegramValidationService {

    @Value("${telegram.bot-token}")
    private String botToken;

    public boolean isValid(String telegramInitData) throws Exception {
        Pair<String, String> result = parseInitData(telegramInitData);
        String hash = result.first();
        String initData = result.second();
        byte[] secretKey = new HmacUtils("HmacSHA256", "WebAppData").hmac(botToken);
        String initDataHash = new HmacUtils("HmacSHA256", secretKey).hmacHex(initData);

        return initDataHash.equals(hash);
    }

    private Pair<String, String> parseInitData(String telegramInitData) throws UnsupportedEncodingException {
        Map<String, String> initData = parseQueryString(telegramInitData);
        initData = sortMap(initData);
        String hash = initData.remove("hash");

        List<String> separatedData = initData.entrySet().stream()
                .map((v) -> v.getKey() + "=" + v.getValue())
                .collect(Collectors.toList());
        return new Pair<>(hash, String.join("\n", separatedData));
    }

    public Map<String, String> parseQueryString(String queryString) throws UnsupportedEncodingException {
        Map<String, String> parameters = new TreeMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), StandardCharsets.UTF_8) : pair;
            String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), StandardCharsets.UTF_8) : null;
            parameters.put(key, value);
        }
        return parameters;
    }

    private static Map<String, String> sortMap(Map<String, String> map) {
        return new TreeMap<>(map);
    }
}