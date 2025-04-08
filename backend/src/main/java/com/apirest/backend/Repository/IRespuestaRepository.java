package com.apirest.backend.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Model.SolicitudModel;

public interface IRespuestaRepository extends JpaRepository<RespuestaModel, Integer> {
    List<RespuestaModel> findBySolicitud(SolicitudModel solicitud);
    List<RespuestaModel> findByRespuestaPadre(RespuestaModel respuestaPadre);
}