package com.apirest.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Exception.RecursoNoEncontradoException;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudesModel;
import com.apirest.backend.Repository.ISolicitudesRepository;

@Service
public class SolicitudesService implements ISolicitudesService {
    @Autowired ISolicitudesRepository solicitudesRepository;

    @Override
    public SolicitudesModel guardarSolicitud(SolicitudesModel solicitud) {
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(EstadoSolicitud.radicada);
        }
        return solicitudesRepository.save(solicitud);
    }

    @Override
    public List<SolicitudesModel> listarSolicitudes() {
        return solicitudesRepository.findAll();
    }

    @Override
    public SolicitudesModel buscarSolicitudPorId(Integer id) {
        return solicitudesRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con ID " + id + " no encontrada."));
    }

    @Override
    public SolicitudesModel actualizarSolicitud(Integer id, SolicitudesModel solicitudActualizada) {
        SolicitudesModel existente = buscarSolicitudPorId(id);

        if (existente.getEstado() != EstadoSolicitud.radicada) {
            throw new IllegalStateException("Solo se pueden editar solicitudes en estado 'radicada'");
        }
        existente.setCategoria(solicitudActualizada.getCategoria());
        existente.setDescripcion(solicitudActualizada.getDescripcion());
        existente.setTipo(solicitudActualizada.getTipo());

        return solicitudesRepository.save(existente);
    }

    @Override
    public List<SolicitudesDTO> listarPorUsuarioId(Integer usuarioId) {
        return solicitudesRepository.findByUsuarioId(usuarioId);
    }

}