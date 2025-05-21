package com.apirest.backend.DTO;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaEmbed {
    private Integer respuestaId;
    private String comentario;
    private Date fechaRespuesta;
}