package com.apirest.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaRepository extends MongoRepository<RespuestaModel, Integer> {
    List<RespuestaModel> findBySolicitudId(Integer solicitudId);
    List<RespuestaModel> findByRespuestaPadre(Integer respuestaPadre);
}
