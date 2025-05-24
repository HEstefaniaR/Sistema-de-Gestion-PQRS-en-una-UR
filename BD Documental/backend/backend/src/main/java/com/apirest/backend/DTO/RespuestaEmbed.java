package com.apirest.backend.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

import org.bson.types.ObjectId;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaEmbed {
    private ObjectId respuestaId;
    private String comentario;
    private Date fechaRespuesta;
}