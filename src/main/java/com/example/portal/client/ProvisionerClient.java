package com.example.portal.client;

import com.example.portal.dto.*;
import com.example.portal.exception.ProvisionerUnavailableException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Map;

@Service
public class ProvisionerClient {

    private final RestClient restClient;

    public ProvisionerClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public AuthResponse register(RegisterRequest request) {
        try {
            return restClient.post()
                    .uri("/auth/register")
                    .body(request)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public AuthResponse login(LoginRequest request) {
        try {
            return restClient.post()
                    .uri("/auth/login")
                    .body(request)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public TokenValidationResponse validateToken(String token) {
        try {
            return restClient.post()
                    .uri("/auth/validate")
                    .body(Map.of("token", token))
                    .retrieve()
                    .body(TokenValidationResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public UsersResponse getAllUsers(String token) {
        try {
            return restClient.get()
                    .uri("/admin/users")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(UsersResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public StatsResponse getSystemStats(String token) {
        try {
            return restClient.get()
                    .uri("/admin/stats")
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(StatsResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public AuthResponse createUser(String token, UserRequest request) {
        try {
            return restClient.post()
                    .uri("/admin/users")
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public AuthResponse updateUser(String token, String userId, UserRequest request) {
        try {
            return restClient.put()
                    .uri("/admin/users/{userId}", userId)
                    .header("Authorization", "Bearer " + token)
                    .body(request)
                    .retrieve()
                    .body(AuthResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }

    public MessageResponse deleteUser(String token, String userId) {
        try {
            return restClient.delete()
                    .uri("/admin/users/{userId}", userId)
                    .header("Authorization", "Bearer " + token)
                    .retrieve()
                    .body(MessageResponse.class);
        } catch (RestClientException e) {
            throw new ProvisionerUnavailableException("Failed to connect to provisioner service", e);
        }
    }
}
