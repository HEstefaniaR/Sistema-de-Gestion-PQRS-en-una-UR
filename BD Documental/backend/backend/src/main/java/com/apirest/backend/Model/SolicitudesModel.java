package com.apirest.backend.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import org.bson.types.ObjectId;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Solicitudes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudesModel {
    @Id
    private ObjectId id;
    private String tipo;
    private String Categoria;
    private String descripcion;
    private EstadoSolicitud estado;
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaActualizacion;
    private ObjectId usuarioId;
    private List<RespuestaResumen> respuestas;
     private List<EvidenciaEmbed> evidencias;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RespuestaResumen {
        private ObjectId respuestaId;
        private String comentario;
        private Date fechaRespuesta;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenciaEmbed {
        private ObjectId idEvidencia;
        private TipoArchivo tipoArchivo;
        private String rutaArchivo;
        private LocalDateTime fechaHoraCarga;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SolicitudResumen {
        private ObjectId SolicitudId;
        private java.util.Date fechaCreacion;
        private String estado;
    }

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }
}