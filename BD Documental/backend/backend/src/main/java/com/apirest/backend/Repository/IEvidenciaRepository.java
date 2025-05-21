package com.apirest.backend.Repository;

import com.apirest.backend.Model.EvidenciaModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IEvidenciaRepository extends MongoRepository<EvidenciaModel, String> {
    List<EvidenciaModel> findByIdSolicitud(String idSolicitud);
}