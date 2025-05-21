package com.apirest.backend.Service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Repository.IRespuestaRepository;

@Service
public class RespuestaServiceImpl implements IRespuestaService {

    @Autowired
    private IRespuestaRepository respuestaRepo;

    @Override
    public String guardarRespuesta(RespuestaModel respuesta) {
        respuestaRepo.save(respuesta);
        return "Respuesta guardada correctamente.";
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
        respuesta.setId(id);
        return respuestaRepo.save(respuesta);
    }

    @Override
    public void eliminarRespuesta(ObjectId id) {
        respuestaRepo.deleteById(id);
    }
}
