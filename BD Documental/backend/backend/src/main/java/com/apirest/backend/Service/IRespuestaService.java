package com.apirest.backend.Service;

import java.util.List;
import org.bson.types.ObjectId;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaService {

    String guardarRespuesta(RespuestaModel respuesta);

    List<RespuestaModel> listarRespuestas();

    RespuestaModel buscarRespuestaPorId(ObjectId id);

    RespuestaModel actualizarRespuesta(ObjectId id, RespuestaModel respuesta);

    String eliminarRespuesta(ObjectId id);
}
