package com.apirest.backend.Model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "usuario")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsuarioModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idUsuario;
    @Column(nullable = false, unique = true)
    private String usuario;
    @Column(name = "nombre_completo", nullable = false)
    private String nombreCompleto;
    @Column(name = "tipo_documento")
    private String tipoDocumento;
    @Column(name = "numero_documento", unique = true)
    private String numeroDocumento;
    @Column(name = "correo_electronico")
    private String correoElectronico;
    private String telefono;
    @Column(name = "direccion_interna")
    private String direccionInterna;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RolUsuario rol;
    private String contrasena;
}