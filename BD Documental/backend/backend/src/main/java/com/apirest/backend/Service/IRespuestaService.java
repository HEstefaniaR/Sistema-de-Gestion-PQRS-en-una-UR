package com.apirest.backend.Service;

import java.util.List;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaService {
    String guardarRespuesta(RespuestaModel respuesta);
    List<RespuestaModel> listarRespuestas();
    RespuestaModel buscarRespuestaPorId(Integer id);
    RespuestaModel actualizarRespuesta(Integer id, RespuestaModel respuesta);
    String eliminarRespuesta(Integer id);
}
