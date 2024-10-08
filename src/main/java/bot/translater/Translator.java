package bot.translater;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

/**
 * Class for translating text from one language to another
 */
public class Translator {
    private static final String BASE_URL = "https://free-google-translator.p.rapidapi.com/external-api/free-google-translator";
    private static final String API_KEY = "87cd6613c2msh462551e1132101bp1c0274jsn373cc2a1e098";
    private static final String API_HOST = "free-google-translator.p.rapidapi.com";

    /**
     * Translate text from one language to another
     * @param text text to translate
     * @param from source language
     * @param to target language
     * @return translated text
     * @throws IOException if something went wrong with request
     * @throws InterruptedException if something went wrong with request
     */
    public String translate(String text, String from, String to) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(String.format("%s?from=%s&to=%s&query=%s", BASE_URL, from, to, URLEncoder.encode(text, StandardCharsets.UTF_8))))
                .header("x-rapidapi-key", API_KEY)
                .header("x-rapidapi-host", API_HOST)
                .header("Content-Type", "application/json")
                .method("POST", HttpRequest.BodyPublishers.ofString("{\"translate\":\"rapidapi\"}"))
                .build();
        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(response.body());
        return jsonNode.path("translation").asText();
    }
}
