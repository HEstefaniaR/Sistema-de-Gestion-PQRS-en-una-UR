package com.apirest.backend.DTO;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudesDTO {
    private ObjectId id;
    private String tipo;
    private String estado;
}