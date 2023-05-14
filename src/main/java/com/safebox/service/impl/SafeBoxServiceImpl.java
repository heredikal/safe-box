package com.safebox.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.safebox.config.exceptions.SafeBoxNotAuthorizedException;
import com.safebox.config.exceptions.SafeBoxNotFoundException;
import com.safebox.config.exceptions.SafeBoxTokenExpiredException;
import com.safebox.controller.dto.SafeBoxDTO;
import com.safebox.integration.entity.SafeBoxEntity;
import com.safebox.integration.mapper.SafeBoxMapper;
import com.safebox.integration.repository.SafeBoxRepository;
import com.safebox.service.SafeBoxService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class SafeBoxServiceImpl implements SafeBoxService {

    @Value("${safe-box.username}")
    private String username;

    @Value("${safe-box.password}")
    private String password;

    @Value("${safe-box.min.expired.token}")
    private Integer minExpiredToken;

    @Value("${safe-box.number.attemps}")
    private Integer numberAttemps;

    private SafeBoxRepository safeBoxRepository;

    private SafeBoxMapper safeBoxMapper;

    private Map<String, LocalDateTime> tokenExpirationMap = new ConcurrentHashMap<>();
    private Map<String, Integer> loginAttemptsMap = new ConcurrentHashMap<>();

    public SafeBoxServiceImpl(SafeBoxRepository safeBoxRepository, SafeBoxMapper safeBoxMapper) {
        this.safeBoxRepository = safeBoxRepository;
        this.safeBoxMapper = safeBoxMapper;
    }

    public String openSafeBox(String username, String password)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException {

        Integer failedAttempts = loginAttemptsMap.getOrDefault(username, 0);
        if (failedAttempts >= numberAttemps) {
            throw new SafeBoxNotAuthorizedException(
                    "Safe box is blocked due to " + failedAttempts + " failed login attempts");
        }
        // to validate username and password a corporate LDAP system could be used
        if (!this.password.equals(password) || !this.username.equals(username)) {
            failedAttempts = loginAttemptsMap.compute(username, (key, value) -> value == null ? 1 : value + 1);
            throw new SafeBoxNotAuthorizedException("Invalid password for safe box with username: " + username);
        }
        loginAttemptsMap.put(username, 0);
        String token = UUID.randomUUID().toString();
        tokenExpirationMap.put(token, LocalDateTime.now().plusMinutes(minExpiredToken));
        log.debug("Token generated: " + token);
        return token;
    }

    public void addItem(String token, SafeBoxDTO safeBoxDTO)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException, SafeBoxTokenExpiredException {
        verifyToken(token);
        SafeBoxEntity safeBoxEntity = safeBoxMapper.convertToEntity(safeBoxDTO);
        safeBoxRepository.save(safeBoxEntity);
        log.debug("Item added to safe box: " + safeBoxEntity.toString());
    }

    public List<SafeBoxDTO> getItems(String token)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException, SafeBoxTokenExpiredException {
        verifyToken(token);
        List<SafeBoxEntity> safeBoxList = safeBoxRepository.findAll();
        List<SafeBoxDTO> safeBoxDTOList = safeBoxList.stream()
                .map(item -> safeBoxMapper.convertToDto(item))
                .toList();

        log.debug("Items retrieved from safe box: {}", safeBoxDTOList.size());
        return safeBoxDTOList;
    }

    private void verifyToken(String token) throws SafeBoxTokenExpiredException {
        // This method can be replaced by a call to an api manager such as keycloak
        LocalDateTime expirationTime = tokenExpirationMap.get(token);
        if (expirationTime == null) {
            throw new SafeBoxTokenExpiredException("Token not found or invalid");
        }
        log.debug("Expiration time for token " + token + ": " + expirationTime.toString());
        if (expirationTime.isBefore(LocalDateTime.now())) {
            throw new SafeBoxTokenExpiredException("Token expired or invalid");
        }
    }

}
