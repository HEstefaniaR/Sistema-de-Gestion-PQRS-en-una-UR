package com.apirest.backend.Repository;
import java.util.Date;
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
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }", 
        "{ $lookup: { from: 'Evidencias', localField: '_id', foreignField: 'idSolicitud', as: 'evidencias' } }", 
            "id: 1, tipo: 1, categoria: 1, descripcion: 1, " +
            "estado: 1, fechaHoraCreacion: 1, " +
            "evidencias: { id: 1, tipoArchivo: 1, rutaArchivo: 1, fechaHoraCarga: 1 } " + 
        "} }"
    })
    SolicitudesModel findSolicitudConEvidencias(Integer id);

    void eliminarSolicitudPorId(Integer id);

    List<SolicitudesModel> findByEstadoAndFechaActualizacionBefore(String estado, Date fecha);
}