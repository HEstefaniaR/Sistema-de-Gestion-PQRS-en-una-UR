package com.apirest.backend.Service;

import java.util.List;
import org.bson.types.ObjectId;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaService {
    RespuestaModel guardarRespuesta(RespuestaModel respuesta);
    List<RespuestaModel> listarRespuestas();
    RespuestaModel buscarRespuestaPorId(ObjectId id);
    RespuestaModel actualizarRespuesta(ObjectId id, RespuestaModel respuesta);
    List<RespuestaModel> listarRespuestasPorSolicitud(ObjectId solicitudId);
    RespuestaModel buscarPrimerRespuestaAdminPorSolicitudId(ObjectId idSolicitud);
    RespuestaModel buscarUltimaReplicaUsuarioPorSolicitudId(ObjectId solicitudId);
}
