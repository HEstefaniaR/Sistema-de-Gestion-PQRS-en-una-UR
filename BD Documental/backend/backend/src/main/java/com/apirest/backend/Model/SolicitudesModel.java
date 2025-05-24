package com.apirest.backend.Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

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
    private String categoria;
    private String descripcion;
    private EstadoSolicitud estado;
    private LocalDateTime fechaHoraCreacion;
    private LocalDateTime fechaActualizacion;
    private ObjectId usuarioId;
    private List<RespuestaResumen> respuestas;
    private List<EvidenciaEmbed> evidencias;

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }

    @JsonProperty("usuarioId")
    public String getUsuarioIdString() {
        return usuarioId != null ? usuarioId.toHexString() : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RespuestaResumen {
        private ObjectId respuestaId;
        private String comentario;
        private Date fechaRespuesta;

        @JsonProperty("respuestaId")
        public String getRespuestaIdString() {
            return respuestaId != null ? respuestaId.toHexString() : null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EvidenciaEmbed {
        @Field("evidenciaId")
        private ObjectId idEvidencia;
        private TipoArchivo tipoArchivo;
        private String rutaArchivo;
        private String descripcion;
        @Field("fechaRegistro")
        private LocalDateTime fechaHoraCarga;

        @JsonProperty("evidenciaId")
        public String getIdEvidenciaString() {
            return idEvidencia != null ? idEvidencia.toHexString() : null;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SolicitudResumen {
        private ObjectId SolicitudId;
        private Date fechaCreacion;
        private String estado;

        @JsonProperty("SolicitudId")
        public String getSolicitudIdString() {
            return SolicitudId != null ? SolicitudId.toHexString() : null;
        }
    }
}