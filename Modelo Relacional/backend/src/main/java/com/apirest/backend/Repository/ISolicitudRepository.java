package com.apirest.backend.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudModel;

public interface ISolicitudRepository extends JpaRepository<SolicitudModel, Integer>{
        List<SolicitudModel> findByEstadoAndFechaActualizacionBefore(EstadoSolicitud estado, LocalDateTime fecha);
}
