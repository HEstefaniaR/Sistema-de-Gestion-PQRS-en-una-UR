package com.apirest.backend.Repository;

import com.apirest.backend.Model.RespuestaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.bson.types.ObjectId;
import java.util.List;

public interface IRespuestaRepository extends MongoRepository<RespuestaModel, ObjectId> {
    List<RespuestaModel> findBySolicitudId(ObjectId solicitudId);
    List<RespuestaModel> findByRespuestaPadre(ObjectId respuestaPadre);
}
