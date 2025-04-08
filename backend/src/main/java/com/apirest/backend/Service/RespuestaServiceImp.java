package com.apirest.backend.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.RespuestaModel;
import com.apirest.backend.Model.SolicitudModel;
import com.apirest.backend.Repository.IRespuestaRepository;

@Service
public class RespuestaServiceImp implements IRespuestaService {

    @Autowired
    private IRespuestaRepository respuestaRepo;

    @Override
    public RespuestaModel guardarRespuesta(RespuestaModel respuesta) {
        return respuestaRepo.save(respuesta);
    }

    @Override
    public List<RespuestaModel> listarRespuestasPorSolicitud(SolicitudModel solicitud) {
        return respuestaRepo.findBySolicitud(solicitud);
    }

    @Override
    public List<RespuestaModel> listarRespuestasPorRespuestaPadre(RespuestaModel respuestaPadre) {
        return respuestaRepo.findByRespuestaPadre(respuestaPadre);
    }

    @Override
    public RespuestaModel buscarRespuestaPorId(Integer id) {
        Optional<RespuestaModel> opt = respuestaRepo.findById(id);
        return opt.orElse(null); 
    }

    @Override
    public RespuestaModel actualizarRespuesta(Integer id, RespuestaModel nuevaRespuesta) {

        Optional<RespuestaModel> optExistente = respuestaRepo.findById(id);

        if (optExistente.isEmpty()) {
            return null; 
        }

        RespuestaModel existente = optExistente.get();

        
        existente.setComentario(nuevaRespuesta.getComentario());
        existente.setRutaOficioPdf(nuevaRespuesta.getRutaOficioPdf());
        existente.setPuntuacion(nuevaRespuesta.getPuntuacion());

    
        return respuestaRepo.save(existente);
    }

    @Override
    public void eliminarRespuestaPorId(Integer id) {
        Optional<RespuestaModel> opt = respuestaRepo.findById(id);

        if (opt.isPresent()) {
            respuestaRepo.deleteById(id); 
        } else {
            throw new IllegalArgumentException("La respuesta con ID " + id + " no existe.");
        }
    }
}
