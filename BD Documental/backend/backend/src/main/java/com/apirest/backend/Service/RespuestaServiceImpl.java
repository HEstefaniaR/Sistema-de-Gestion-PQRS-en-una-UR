package com.apirest.backend.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Model.SolicitudesModel.RespuestaResumen;
import com.apirest.backend.Repository.IRespuestaRepository;


@Service
public class RespuestaServiceImpl implements IRespuestaService {

    @Autowired
    private IRespuestaRepository respuestaRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public RespuestaModel guardarRespuesta(RespuestaModel respuesta) {
        ObjectId solicitudId = respuesta.getSolicitudId();
        if (solicitudId == null) {
            throw new IllegalArgumentException("El ID de la solicitud en la respuesta es nulo.");
        }

        boolean solicitudExiste = mongoTemplate.exists(
            new Query(Criteria.where("_id").is(solicitudId)),
            "Solicitudes"
        );
        if (!solicitudExiste) {
            throw new IllegalArgumentException("La solicitud con ID " + solicitudId + " no existe.");
        }

        respuestaRepo.save(respuesta);

        RespuestaResumen embed = new RespuestaResumen(
            respuesta.getId(),
            respuesta.getComentario(),
            new Date()
        );

        Update update = new Update().push("respuestas", embed);
        mongoTemplate.updateFirst(
            new Query(Criteria.where("_id").is(solicitudId)),
            update,
            "Solicitudes"
        );

        return respuesta;
    }

    @Override
    public List<RespuestaModel> listarRespuestas() {
        return respuestaRepo.findAll();
    }

    @Override
    public RespuestaModel buscarRespuestaPorId(ObjectId id) {
        Optional<RespuestaModel> respuesta = respuestaRepo.findById(id);
        return respuesta.orElse(null);
    }

    @Override
    public RespuestaModel actualizarRespuesta(ObjectId id, RespuestaModel respuesta) {
        respuestaRepo.save(respuesta);

        ObjectId solicitudId = respuesta.getSolicitudId();
        if (solicitudId == null) {
            throw new IllegalArgumentException("El ID de la solicitud en la respuesta es nulo.");
        }

        Query query = new Query(
            Criteria.where("_id").is(solicitudId)
                .and("respuestas.respuestaId").is(respuesta.getId())
        );

        Update update = new Update()
            .set("respuestas.$.comentario", respuesta.getComentario())
            .set("respuestas.$.fechaRespuesta", new Date());

        mongoTemplate.updateFirst(query, update, "Solicitudes");

        return respuesta;
    }

    @Override
    public List<RespuestaModel> listarRespuestasPorSolicitud(ObjectId solicitudId) {
        return respuestaRepo.findBySolicitudId(solicitudId);
    }

    @Override
    public RespuestaModel buscarPrimerRespuestaAdminPorSolicitudId(ObjectId idSolicitud) {
        return respuestaRepo.findFirstBySolicitudIdAndAutorTipoAndRespuestaPadreIsNullOrderByFechaRespuestaAsc(idSolicitud, "admin")
                                .orElse(null);
    }

    @Override
    public RespuestaModel buscarUltimaReplicaUsuarioPorSolicitudId(ObjectId solicitudId) {
        return respuestaRepo.findFirstBySolicitudIdAndAutorTipoAndRespuestaPadreNotNullOrderByFechaRespuestaDesc(
            solicitudId, "usuario"
        );
    }
}