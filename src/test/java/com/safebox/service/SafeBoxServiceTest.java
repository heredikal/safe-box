package com.safebox.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;

import com.safebox.config.exceptions.SafeBoxNotAuthorizedException;
import com.safebox.controller.dto.SafeBoxDTO;
import com.safebox.integration.entity.SafeBoxEntity;
import com.safebox.integration.mapper.SafeBoxMapper;
import com.safebox.integration.repository.SafeBoxRepository;
import com.safebox.service.impl.SafeBoxServiceImpl;
import com.safebox.utils.Fixtures;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
public class SafeBoxServiceTest {

    @MockBean
    private SafeBoxRepository safeBoxRepository;

    @Mock
    private SafeBoxMapper safeBoxMapper;

    private SafeBoxServiceImpl safeBoxService;

    @Value("${safe-box.username}")
    private String username;

    @Value("${safe-box.password}")
    private String password;

    @Value("${safe-box.min.expired.token}")
    private Integer minExpiredToken;

    @Value("${safe-box.number.attemps}")
    private Integer numberAttemps;

    @BeforeEach
    void setup() {
        safeBoxService = new SafeBoxServiceImpl(safeBoxRepository, safeBoxMapper);
        ReflectionTestUtils.setField(safeBoxService, "username", username);
        ReflectionTestUtils.setField(safeBoxService, "password", password);
        ReflectionTestUtils.setField(safeBoxService, "minExpiredToken", minExpiredToken);
        ReflectionTestUtils.setField(safeBoxService, "numberAttemps", numberAttemps);
    }

    @Test
    void testOpenSafeBoxWithValidCredentials() throws Exception {
        String token = safeBoxService.openSafeBox("username", "password");
        assertNotNull(token);
    }

    @Test
    public void testOpenSafeBoxWithInvalidCredentials() {
        String invalidPassword = "invalidPassword";
        String invalidUsername = "invalidUsername";
        assertThrows(SafeBoxNotAuthorizedException.class, () -> {
            safeBoxService.openSafeBox(invalidUsername, password);
        });
        assertThrows(SafeBoxNotAuthorizedException.class, () -> {
            safeBoxService.openSafeBox(username, invalidPassword);
        });
    }

    @Test
    public void testOpenSafeBoxBlockedAccount() throws Exception {
        for (int i = 0; i < numberAttemps; i++) {
            assertThrows(SafeBoxNotAuthorizedException.class, () -> {
                safeBoxService.openSafeBox("randomUsername", "randomPassword");
            });
        }
        assertThrows(SafeBoxNotAuthorizedException.class, () -> {
            safeBoxService.openSafeBox("randomUsername", "randomPassword");
        });
    }

    @Test
    public void testAddItem() throws Exception {
        // Given
        String token = safeBoxService.openSafeBox("username", "password");
        SafeBoxDTO safeBoxDTO = Fixtures.getObject(SafeBoxDTO.class);
        SafeBoxEntity safeBoxEntity = Fixtures.getObject(SafeBoxEntity.class);
        when(safeBoxMapper.convertToEntity(safeBoxDTO)).thenReturn(safeBoxEntity);

        // When
        safeBoxService.addItem(token, safeBoxDTO);

        // Then
        verify(safeBoxMapper).convertToEntity(safeBoxDTO);
        verify(safeBoxRepository).save(safeBoxEntity);
    }

    @Test
    public void testGetItems() throws Exception {
        // Given
        String token = safeBoxService.openSafeBox("username", "password");
        List<SafeBoxEntity> safeBoxList = Collections.singletonList(new SafeBoxEntity());
        List<SafeBoxDTO> safeBoxDTOList = Collections.singletonList(new SafeBoxDTO());
        when(safeBoxRepository.findAll()).thenReturn(safeBoxList);
        when(safeBoxMapper.convertToDto(safeBoxList.get(0))).thenReturn(safeBoxDTOList.get(0));
        
        // When
        List<SafeBoxDTO> result = safeBoxService.getItems(token);

        // Then
        verify(safeBoxRepository).findAll();
        verify(safeBoxMapper).convertToDto(safeBoxList.get(0));
        assertEquals(safeBoxDTOList, result);
    }
}