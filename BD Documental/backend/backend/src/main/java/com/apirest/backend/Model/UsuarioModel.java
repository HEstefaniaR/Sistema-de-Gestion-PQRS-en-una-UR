package com.apirest.backend.Model;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document ("Usuarios")
public class UsuarioModel {
    @Id
    private Integer id;
    private String usuario;
    private String contrasena;
    private String nombreCompleto;
    private String tipoDocumento;
    private String numeroDocumento;
    private String correo;
    private String telefono;
    private DireccionUsuario direccion; 
    private RolUsuario rol;
    private List<SolicitudesModel> solicitudes = new ArrayList<>();

}
