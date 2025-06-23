//package org.example.backendproject.stompwebsocket.gpt;
//
//
//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.springframework.stereotype.Service;
//
//import java.net.URI;
//import java.net.http.HttpClient;
//import java.net.http.HttpRequest;
//import java.net.http.HttpResponse;
//import java.util.HashMap;
//import java.util.Map;
//
//@Service
//public class GPTService {
//
//    //json문자열 <-> 자바객체, json객체
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    public String gptMessage(String message) throws Exception{
//
//        //API 호출을 위한 본문 작성
//        Map<String,Object> requestBody  = new HashMap<>();
//        requestBody .put("model","gpt-4.1");
//        requestBody .put("input",message);
//
//        //http 요청 작성
//        HttpRequest request = HttpRequest.newBuilder()
//                .uri(URI.create("https://api.openai.com/v1/responses"))
//                .header("Authorization","")
//                .header("Content-Type","application/json")
//                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody))) //본문 삽입
//                .build();
//
//
//        //요청 전송 및 응답 수신
//        HttpClient  client = HttpClient.newHttpClient();
//        HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());
//
//        //응답을 Json으로 파싱
//        JsonNode jsonNode = mapper.readTree(response.body());
//        System.out.println("gpt 응답 : "+jsonNode);
//
//        //메세지 부분만 추출하여 반환
//        String gptMessageResponse = jsonNode.get("output").get(0).get("content").get(0).get("text").asText();
//        return gptMessageResponse;
//
//    }
//
//}

package org.example.backendproject.stompwebsocket.gpt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GPTService {

    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${API_KEY}")
    private String apiKey; // application.yml 또는 .properties에 설정

    public String gptMessage(String message) throws Exception {
        // 요청 JSON 구조 구성
        Map<String, Object> requestBody = new HashMap<>();

        Map<String, Object> part = Map.of("text", message);
        Map<String, Object> content = Map.of(
                "role", "user",
                "parts", List.of(part)
        );

        requestBody.put("contents", List.of(content));

        // HTTP 요청 구성
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=" + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(requestBody)))
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] Gemini raw response: " + response.body());

        JsonNode jsonNode = mapper.readTree(response.body());

        JsonNode candidates = jsonNode.path("candidates");
        if (candidates.isMissingNode() || !candidates.isArray() || candidates.size() == 0) {
            return "[Gemini 응답 없음]";
        }

        return candidates
                .get(0)
                .path("content")
                .path("parts")
                .get(0)
                .path("text")
                .asText("[텍스트 없음]");
    }

}