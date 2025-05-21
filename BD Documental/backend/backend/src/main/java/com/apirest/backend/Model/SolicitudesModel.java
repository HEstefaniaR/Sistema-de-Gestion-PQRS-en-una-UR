package com.apirest.backend.Model;

import java.time.LocalDateTime;
import java.util.List;

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

     private List<EvidenciaEmbed> evidencias;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenciaEmbed {
        private String idEvidencia;
        private TipoArchivo tipoArchivo;
        private String rutaArchivo;
        private LocalDateTime fechaHoraCarga;
    }
}