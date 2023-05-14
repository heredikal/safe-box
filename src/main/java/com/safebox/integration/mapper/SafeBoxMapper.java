package com.safebox.integration.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.safebox.controller.dto.SafeBoxDTO;
import com.safebox.integration.entity.SafeBoxEntity;

@Component
public class SafeBoxMapper {

    @Autowired
    private ModelMapper modelMapper;

    public SafeBoxDTO convertToDto(SafeBoxEntity safeBox) {
        return modelMapper.map(safeBox, SafeBoxDTO.class);
    }

    public SafeBoxEntity convertToEntity(SafeBoxDTO safeBoxDTO) {
        return modelMapper.map(safeBoxDTO, SafeBoxEntity.class);
    }

}
