package com.example.pharmacy.service;

import com.example.pharmacy.dto.VentaDTO;

import java.util.List;

public interface VentaService {

    VentaDTO createVenta(VentaDTO dto);

    List<VentaDTO> findAll();

    VentaDTO getVentaWithDetails(Long idVenta);

    VentaDTO updateVenta(Long idVenta, VentaDTO dto);

    void deleteVenta(Long idVenta);
}



