package com.example.securerest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = {
        "spring.security.user.name=test-user"
})
class SecureRestApplicationTests {

    @DynamicPropertySource
    static void credentials(DynamicPropertyRegistry registry) {
        registry.add("spring.security.user.password", () -> "test-password");
    }

    @LocalServerPort
    private int port;

    private final HttpClient client = HttpClient.newHttpClient();

    @Test
    void anonymousRequestsAreChallenged() throws Exception {
        HttpResponse<String> response = request(null);

        assertEquals(401, response.statusCode());
        assertTrue(response.headers().firstValue("WWW-Authenticate").orElse("").startsWith("Basic"));
    }

    @Test
    void incorrectCredentialsAreRejected() throws Exception {
        assertEquals(401, request(basicCredentials("test-user", "wrong-password")).statusCode());
        assertEquals(401, request(basicCredentials("wrong-user", "test-password")).statusCode());
    }

    @Test
    void correctCredentialsCanAccessTheEndpoint() throws Exception {
        HttpResponse<String> response = request(basicCredentials("test-user", "test-password"));

        assertEquals(200, response.statusCode());
        assertTrue(response.headers().firstValue("Content-Type").orElse("").startsWith("application/json"));
        assertEquals("{\"message\":\"Hello, world!\"}", response.body());
    }

    private HttpResponse<String> request(String authorization) throws Exception {
        HttpRequest.Builder request = HttpRequest.newBuilder(URI.create("http://localhost:" + port + "/api/hello"))
                .GET();
        if (authorization != null) {
            request.header("Authorization", authorization);
        }
        return client.send(request.build(), HttpResponse.BodyHandlers.ofString());
    }

    private String basicCredentials(String username, String password) {
        String credentials = username + ":" + password;
        return "Basic " + Base64.getEncoder().encodeToString(credentials.getBytes(StandardCharsets.UTF_8));
    }
}
