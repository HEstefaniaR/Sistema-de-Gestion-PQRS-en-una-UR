package com.apirest.backend.Model;

import java.util.Date;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@Document("Respuestas")
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaModel {

    @Id
    private ObjectId id;

    private ObjectId solicitudId;
    private String comentario;
    private String rutaArchivoPDF;
    private Date fechaRespuesta;
    private Integer puntuacion;
    private ObjectId respuestaPadre;
    private Autor autor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Autor {
        private String tipo;
        private ObjectId autorId;
    }

    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }
}
