package com.apirest.backend.Model;

import java.time.LocalDateTime;

import org.bson.types.ObjectId;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudResumen {
    private ObjectId SolicitudId;
    private LocalDateTime fechaCreacion;
    private String estado;
    
    @JsonProperty("SolicitudId")
    public String getSolicitudId() {
        return SolicitudId != null ? SolicitudId.toHexString() : null;
    }
}