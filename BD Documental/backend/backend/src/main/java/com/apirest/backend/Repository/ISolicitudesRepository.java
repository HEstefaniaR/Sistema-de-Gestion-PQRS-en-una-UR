package com.apirest.backend.Repository;
import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesRepository extends MongoRepository<SolicitudesModel, Integer> {
    @Aggregation(pipeline = {
        "{ $match: { usuarioId: ?0 } }",
        "{ $project: { id: 1, tipo: 1, estado: 1 } }"
    })
    List<SolicitudesDTO> findByUsuarioId(int usuarioId);

    void eliminarSolicitudPorId(Integer id);
}