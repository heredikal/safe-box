package com.safebox.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.safebox.config.exceptions.SafeBoxNotAuthorizedException;
import com.safebox.config.exceptions.SafeBoxTokenExpiredException;
import com.safebox.controller.dto.SafeBoxDTO;
import com.safebox.service.SafeBoxService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/safe-box")
@Slf4j
public class SafeBoxController {

    private SafeBoxService safeBoxService;

    public SafeBoxController(SafeBoxService safeBoxService) {
        this.safeBoxService = safeBoxService;
    }

    @PostMapping("/open")
    public ResponseEntity<String> openSafeBox(@RequestParam String username, @RequestParam String password) {
        try {
            log.debug("Attempting to open safe box for user {}", username);
            String token = safeBoxService.openSafeBox(username, password);
            if (token != null) {
                log.debug("Safe box opened successfully for user {}", username);
                HttpHeaders headers = new HttpHeaders();
                headers.add("Authorization", "Bearer " + token);
                return ResponseEntity.ok().headers(headers).build();
            } else {
                log.debug("Failed to open safe box for user {}", username);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } catch (SafeBoxNotAuthorizedException e) {
            log.debug("Failed to open safe box [{}] for user {}", e.getMessage(), username);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/items")
    @Operation(summary = "Get user info by access token", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<String> addItemsToSafeBox(@RequestBody SafeBoxDTO safeBoxDTO, HttpServletRequest request) {
        String token = getTokenHeader(request);
        log.debug("Attempting to add item to safe box for token {}", token);
        try {
            safeBoxService.addItem(token, safeBoxDTO);
            log.debug("Item added to safe box for token {}", token);
            return ResponseEntity.ok().build();
        } catch (SafeBoxTokenExpiredException | SafeBoxNotAuthorizedException e) {
            log.debug("Failed to add item to safe box for token {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/items")
    @Operation(summary = "Get user info by access token", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<List<SafeBoxDTO>> getItemsFromSafeBox(HttpServletRequest request) {
        String token = getTokenHeader(request);
        log.debug("Attempting to get items from safe box for token {}", token);
        try {
            List<SafeBoxDTO> items = safeBoxService.getItems(token);
            log.debug("Items retrieved from safe box for token {}", token);
            return ResponseEntity.ok(items);
        } catch (SafeBoxTokenExpiredException | SafeBoxNotAuthorizedException e) {
            log.debug("Failed to retrieve items from safe box for token {}", token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private String getTokenHeader(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return token;
    }
}
