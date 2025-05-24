package com.apirest.backend.Repository;

import com.apirest.backend.Model.RespuestaModel;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;
import java.util.List;
import java.util.Optional;

public interface IRespuestaRepository extends MongoRepository<RespuestaModel, ObjectId> {
    List<RespuestaModel> findBySolicitudId(ObjectId solicitudId);
    List<RespuestaModel> findByRespuestaPadre(ObjectId respuestaPadre);
    Optional<RespuestaModel> findFirstBySolicitudIdAndAutorTipoAndRespuestaPadreIsNullOrderByFechaRespuestaAsc(ObjectId solicitudId, String tipo);
    RespuestaModel findFirstBySolicitudIdAndAutorTipoAndRespuestaPadreNotNullOrderByFechaRespuestaDesc(ObjectId solicitudId, String autorTipo);
}
