package com.example.portal.controller;

import com.example.portal.client.ProvisionerClient;
import com.example.portal.dto.*;
import com.example.portal.exception.ProvisionerUnavailableException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/proxy")
public class ApiController {

    private final ProvisionerClient provisionerClient;

    public ApiController(ProvisionerClient provisionerClient) {
        this.provisionerClient = provisionerClient;
    }

    @GetMapping("/admin/users")
    public ResponseEntity<?> getUsers(HttpSession session) {
        String token = getToken(session);
        if (token == null) return unauthorized();
        try {
            return ResponseEntity.ok(provisionerClient.getAllUsers(token));
        } catch (ProvisionerUnavailableException e) {
            return serviceUnavailable(e);
        }
    }

    @GetMapping("/admin/stats")
    public ResponseEntity<?> getStats(HttpSession session) {
        String token = getToken(session);
        if (token == null) return unauthorized();
        try {
            return ResponseEntity.ok(provisionerClient.getSystemStats(token));
        } catch (ProvisionerUnavailableException e) {
            return serviceUnavailable(e);
        }
    }

    @PostMapping("/admin/users")
    public ResponseEntity<?> createUser(HttpSession session, @RequestBody UserRequest request) {
        String token = getToken(session);
        if (token == null) return unauthorized();
        try {
            return ResponseEntity.ok(provisionerClient.createUser(token, request));
        } catch (ProvisionerUnavailableException e) {
            return serviceUnavailable(e);
        }
    }

    @PutMapping("/admin/users/{id}")
    public ResponseEntity<?> updateUser(HttpSession session, @PathVariable String id, @RequestBody UserRequest request) {
        String token = getToken(session);
        if (token == null) return unauthorized();
        try {
            return ResponseEntity.ok(provisionerClient.updateUser(token, id, request));
        } catch (ProvisionerUnavailableException e) {
            return serviceUnavailable(e);
        }
    }

    @DeleteMapping("/admin/users/{id}")
    public ResponseEntity<?> deleteUser(HttpSession session, @PathVariable String id) {
        String token = getToken(session);
        if (token == null) return unauthorized();
        try {
            return ResponseEntity.ok(provisionerClient.deleteUser(token, id));
        } catch (ProvisionerUnavailableException e) {
            return serviceUnavailable(e);
        }
    }

    private String getToken(HttpSession session) {
        return (String) session.getAttribute("authToken");
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of("success", false, "message", "Not authenticated"));
    }

    private ResponseEntity<?> serviceUnavailable(ProvisionerUnavailableException e) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of("success", false, "message", "Provisioner service unavailable"));
    }
}
