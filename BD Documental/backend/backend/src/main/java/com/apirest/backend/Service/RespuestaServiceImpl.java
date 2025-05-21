package com.apirest.backend.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import com.apirest.backend.DTO.RespuestaEmbed;
import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Repository.IRespuestaRepository;

@Service
public class RespuestaServiceImpl implements IRespuestaService {

    @Autowired
    private IRespuestaRepository respuestaRepo;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String guardarRespuesta(RespuestaModel respuesta) {
        respuestaRepo.save(respuesta);

        Integer solicitudId = respuesta.getSolicitudId();
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

        RespuestaEmbed embed = new RespuestaEmbed(
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

        return "Respuesta guardada y agregada a la solicitud correctamente.";
    }

    @Override
    public List<RespuestaModel> listarRespuestas() {
        return respuestaRepo.findAll();
    }

    @Override
    public RespuestaModel buscarRespuestaPorId(Integer id) {
        Optional<RespuestaModel> respuesta = respuestaRepo.findById(id);
        return respuesta.orElse(null);
    }

    @Override
    public RespuestaModel actualizarRespuesta(Integer id, RespuestaModel respuesta) {
        respuestaRepo.save(respuesta);

        Integer solicitudId = respuesta.getSolicitudId();
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
    public String eliminarRespuesta(Integer id) {
        RespuestaModel respuesta = respuestaRepo.findById(id).orElse(null);
        if (respuesta == null) {
            throw new IllegalArgumentException("La respuesta con ID " + id + " no existe.");
        }

        respuestaRepo.deleteById(id);

        Integer solicitudId = respuesta.getSolicitudId();
        if (solicitudId != null) {
            Query query = new Query(Criteria.where("_id").is(solicitudId));
            Update update = new Update().pull("respuestas", new Document("respuestaId", respuesta.getId()));
            mongoTemplate.updateFirst(query, update, "Solicitudes");
        }

        return "Respuesta eliminada correctamente.";
    }
}