package com.safebox.controller;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.safebox.config.exceptions.SafeBoxNotAuthorizedException;
import com.safebox.config.exceptions.SafeBoxTokenExpiredException;
import com.safebox.controller.dto.SafeBoxDTO;
import com.safebox.service.SafeBoxService;

@ExtendWith(MockitoExtension.class)
public class SafeBoxControllerTest {

    private SafeBoxService safeBoxService;
    private SafeBoxController safeBoxController;

    @BeforeEach
    public void setUp() {
        safeBoxService = mock(SafeBoxService.class);
        safeBoxController = new SafeBoxController(safeBoxService);
    }

    @Test
    public void testOpenSafeBoxSuccess() throws Exception {
        // Given 
        String token = "token123";
        String username = "user1";
        String password = "pass1";
        
        when(safeBoxService.openSafeBox(username, password)).thenReturn(token);

        // When
        ResponseEntity<String> response = safeBoxController.openSafeBox(username, password);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getHeaders().get("Authorization"));
        Assertions.assertEquals("Bearer "+token, response.getHeaders().get("Authorization").get(0));
    }

    @Test
    public void testOpenSafeBoxFailure() throws Exception {
        // Given 
        String username = "user1";
        String password = "wrongpass";
        
        when(safeBoxService.openSafeBox(username, password)).thenReturn(null);

        // When
        ResponseEntity<String> response = safeBoxController.openSafeBox(username, password);

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void testAddItemsToSafeBoxSuccess() throws Exception {
        // Given 
        String token = "token123";
        SafeBoxDTO safeBoxDTO = new SafeBoxDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer "+token);
        
        // When
        ResponseEntity<String> response = safeBoxController.addItemsToSafeBox(safeBoxDTO, request);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void testAddItemsToSafeBoxFailure() throws Exception {
        // Given 
        String token = "invalid_token";
        SafeBoxDTO safeBoxDTO = new SafeBoxDTO();
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer "+token);
        
        doThrow(new SafeBoxTokenExpiredException(token))
            .when(safeBoxService).addItem(token, safeBoxDTO);
    
        // When
        ResponseEntity<String> response = safeBoxController.addItemsToSafeBox(safeBoxDTO, request);
    
        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
    

    @Test
    public void testGetItemsFromSafeBoxSuccess() throws Exception {
        // Given 
        String token = "valid_token";
        List<SafeBoxDTO> items = new ArrayList<SafeBoxDTO>();
        items.add(new SafeBoxDTO());
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer "+token);
        when(safeBoxService.getItems(token)).thenReturn(items);

        // When
        ResponseEntity<List<SafeBoxDTO>> response = safeBoxController.getItemsFromSafeBox(request);

        // Then
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertEquals(items, response.getBody());
    }

    @Test
    public void testGetItemsFromSafeBoxFailure() throws Exception {
        // Given 
        String token = "invalid_token";
        HttpServletRequest request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer "+token);
        when(safeBoxService.getItems(token)).thenThrow(new SafeBoxNotAuthorizedException());

        // When
        ResponseEntity<List<SafeBoxDTO>> response = safeBoxController.getItemsFromSafeBox(request);

        // Then
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }
}
