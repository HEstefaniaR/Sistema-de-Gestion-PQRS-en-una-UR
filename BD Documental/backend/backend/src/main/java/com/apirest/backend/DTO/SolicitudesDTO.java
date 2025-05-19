package com.apirest.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudesDTO {
    private Integer id;
    private String tipo;
    private String estado;
}