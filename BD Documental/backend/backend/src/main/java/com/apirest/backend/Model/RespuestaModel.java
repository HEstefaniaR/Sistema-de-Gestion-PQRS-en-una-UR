package com.apirest.backend.Model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document("Respuestas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaModel {

    @Id
    private ObjectId id;

    private Integer solicitudId;
    private String comentario;
    private String rutaArchivoPDF;
    private Date fechaRespuesta;
    private Integer puntuacion;
    private Integer respuestaPadre;
    private Autor autor;

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Autor {
        private String tipo;
        private Integer autorId;
    }
}
