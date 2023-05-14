package com.safebox.service;

import java.util.List;

import com.safebox.config.exceptions.SafeBoxNotAuthorizedException;
import com.safebox.config.exceptions.SafeBoxNotFoundException;
import com.safebox.config.exceptions.SafeBoxTokenExpiredException;
import com.safebox.controller.dto.SafeBoxDTO;

public interface SafeBoxService {

    String openSafeBox(String username, String password)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException;

    void addItem(String token, SafeBoxDTO safeBoxDTO)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException, SafeBoxTokenExpiredException;

    List<SafeBoxDTO> getItems(String token)
            throws SafeBoxNotFoundException, SafeBoxNotAuthorizedException, SafeBoxTokenExpiredException;

}
