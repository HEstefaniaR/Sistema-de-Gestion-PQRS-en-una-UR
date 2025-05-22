package com.apirest.backend.Repository;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaRepository extends MongoRepository<RespuestaModel, ObjectId> {
    List<RespuestaModel> findBySolicitudId(ObjectId solicitudId);
    List<RespuestaModel> findByRespuestaPadre(ObjectId respuestaPadre);
}
