package com.apirest.backend.Model;

import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Document("Respuestas")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaModel {

    @Id
    private Integer id;

    private Integer solicitudId;
    private String comentario;
    private String rutaArchivoPDF;
    private Date fechaRespuesta;
    private Integer puntuacion;
    private Integer respuestaPadre;
    private Autor autor;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Autor {
        private String tipo;
        private Integer autorId;
    }
}
