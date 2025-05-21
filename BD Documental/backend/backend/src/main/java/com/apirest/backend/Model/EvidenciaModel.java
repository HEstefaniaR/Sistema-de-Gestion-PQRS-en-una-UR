package com.apirest.backend.Model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document("Evidencias")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EvidenciaModel {
    @Id
    private String id;  
    
    @Field("tipo_archivo")
    private TipoArchivo tipoArchivo;
    
    @Field("rutaarchivo")
    private String rutaArchivo;
    
    private String descripcion;
    
    @Field("fecha_hora_carga")
    private LocalDateTime fechaHoraCarga;
    
    @Field("idsolicitud")
    private String idSolicitud;  
}
    

