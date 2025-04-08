package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Model.SolicitudModel;

public interface IRespuestaService {

    // Guardar una respuesta
    RespuestaModel guardarRespuesta(RespuestaModel respuesta);

    List<RespuestaModel> listarRespuestasPorSolicitud(SolicitudModel solicitud);

    List<RespuestaModel> listarRespuestasPorRespuestaPadre(RespuestaModel respuestaPadre);

    RespuestaModel buscarRespuestaPorId(Integer id);

    RespuestaModel actualizarRespuesta(Integer id, RespuestaModel nuevaRespuesta);

    void eliminarRespuestaPorId(Integer id);
}