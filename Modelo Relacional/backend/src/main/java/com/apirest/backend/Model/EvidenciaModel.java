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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name = "Evidencia")
@Data
@NoArgsConstructor
@AllArgsConstructor


public class EvidenciaModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idevidencia;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoArchivo tipo_archivo;

    @Column(nullable = false, length = 255)
    private String rutaarchivo;

    @Column(columnDefinition = "Text")
    private String descripcion;

    @Column(nullable = false)
    private LocalDateTime fecha_hora_carga;

    @ManyToOne
    @JoinColumn(nullable = false, name = "idsolicitud")
    private SolicitudModel solicitud;
}
