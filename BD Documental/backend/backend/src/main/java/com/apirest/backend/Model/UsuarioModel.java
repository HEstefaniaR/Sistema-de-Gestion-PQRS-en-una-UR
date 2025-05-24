package com.apirest.backend.Model;

import java.util.ArrayList;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document ("Usuarios")
public class UsuarioModel {
    @Id
    private ObjectId id;
    private String usuario;
    private String contrasena;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String correo;
    private String telefono;
    private DireccionUsuario direccion; 
    private RolUsuario rol;
    private List<SolicitudResumen> solicitudes = new ArrayList<>();
    
    @JsonProperty("id")
    public String getIdString() {
        return id != null ? id.toHexString() : null;
    }


}
