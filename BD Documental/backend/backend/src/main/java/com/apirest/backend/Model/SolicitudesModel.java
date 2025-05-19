package com.apirest.backend.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudesModel {
    @Id
    private Integer id;
    private String tipo;
    private String Categoria;
    private String descripcion;
    private EstadoSolicitud estado;
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaActualizacion;
    private Integer usuarioId;
}