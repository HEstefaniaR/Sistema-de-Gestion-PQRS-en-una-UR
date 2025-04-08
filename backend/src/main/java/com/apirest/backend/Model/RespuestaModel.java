package com.apirest.backend.Model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "respuesta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idRespuesta;

    @Column(name = "ruta_oficio_pdf")
    private String rutaOficioPdf;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String comentario;

    @Column(name = "fecha_respuesta", nullable = false)
    private LocalDateTime fechaRespuesta = LocalDateTime.now();

    private Integer puntuacion;

    @ManyToOne
    @JoinColumn(name = "idsolicitud", nullable = false)
    private SolicitudModel solicitud;

    @ManyToOne
    @JoinColumn(name = "idadmin")
    private AdminModel admin;

    @ManyToOne
    @JoinColumn(name = "respuestaid")
    private RespuestaModel respuestaPadre;
    
    @ManyToOne
    @JoinColumn(name = "idusuario")
    private UsuarioModel usuario;
}