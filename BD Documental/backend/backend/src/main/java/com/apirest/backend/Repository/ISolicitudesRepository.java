package com.apirest.backend.Repository;
import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.apirest.backend.Model.SolicitudesModel;

public interface ISolicitudesRepository extends MongoRepository<SolicitudesModel, ObjectId> {
    @Aggregation(pipeline = {
        "{ $match: { usuarioId: ?0 } }",
        "{ $project: { id: 1, tipo: 1, estado: 1 } }"
    })
    List<SolicitudesModel.SolicitudResumen> findResumenByUsuarioId(ObjectId usuarioId);
    @Aggregation(pipeline = {
        "{ $match: { _id: ?0 } }", 
        "{ $lookup: { from: 'Evidencias', localField: '_id', foreignField: 'idSolicitud', as: 'evidencias' } }", 
        "{ $project: { " +
            "id: 1, tipo: 1, categoria: 1, descripcion: 1, estado: 1, " +
            "fechaHoraCreacion: 1, fechaActualizacion: 1, usuarioId: 1, " +
            "respuestas: 1, " +  
            "evidencias: { evidenciaId: 1, tipoArchivo: 1, rutaArchivo: 1, descripcion: 1, fechaRegistro: 1 } " +
        "} }"
    })
SolicitudesModel findSolicitudConEvidencias(ObjectId id);

    void deleteById(ObjectId id);

    List<SolicitudesModel> findByEstadoAndFechaActualizacionBefore(String estado, Date fecha);
}