package com.apirest.backend.Model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "solicitud")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class SolicitudModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idSolicitud;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoSolicitud tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CategoriaSolicitud categoria;

    @Lob
    @Column(nullable = false)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitud estado;

    @Column(name = "fecha_hora_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaHoraCreacion = LocalDateTime.now();

    @Column(name = "fecha_actualizacion", insertable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne
    @JoinColumn(name = "idusuario")
    private UsuarioModel usuario;

}

