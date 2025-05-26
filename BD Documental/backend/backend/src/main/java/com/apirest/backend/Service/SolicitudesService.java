package com.apirest.backend.Service;
import java.io.IOException; 
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.Exception.RecursoNoEncontradoException;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudResumen;
import com.apirest.backend.Model.SolicitudesModel;
import com.apirest.backend.Model.SolicitudesModel.EvidenciaEmbed;
import com.apirest.backend.Model.TipoArchivo;
import com.apirest.backend.Repository.ISolicitudesRepository;
import com.apirest.backend.Repository.IUsuarioRepository;

@Service
public class SolicitudesService implements ISolicitudesService {

    @Autowired 
    private ISolicitudesRepository solicitudesRepository;
    
    @Autowired
    private AlmacenamientoService almacenamientoService;

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public SolicitudesModel guardarSolicitud(SolicitudesModel solicitud) {
        if (solicitud.getEstado() == null) {
            solicitud.setEstado(EstadoSolicitud.Radicada);
        }
        SolicitudesModel guardada = solicitudesRepository.save(solicitud);

        ObjectId usuarioId = guardada.getUsuarioId();
        usuarioRepository.findById(usuarioId).ifPresent(usuario -> {
            if (usuario.getSolicitudes() == null) {
                usuario.setSolicitudes(new ArrayList<>());
            }
            SolicitudResumen resumen = new SolicitudResumen(
                guardada.getId(),
                guardada.getFechaHoraCreacion(),
                guardada.getEstado().toString()
            );
            usuario.getSolicitudes().add(resumen);
            usuarioRepository.save(usuario);
        });

        return guardada;
    }

    

    @Override
    public List<SolicitudesModel> listarSolicitudes() {
        return solicitudesRepository.findAll();
    }

    @Override
    public SolicitudesModel buscarSolicitudPorId(ObjectId id) {
        return solicitudesRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud con ID " + id + " no encontrada."));
    }

    public SolicitudesModel buscarSolicitudConEvidencias(ObjectId id) {
        return solicitudesRepository.findSolicitudConEvidencias(id);
    }

    @Override
    public SolicitudesModel actualizarSolicitud(ObjectId id, SolicitudesModel solicitudActualizada) {
        SolicitudesModel existente = buscarSolicitudPorId(id);

        if (existente.getEstado() == EstadoSolicitud.Cerrada) {
            throw new IllegalStateException("No se puede editar una solicitud en estado 'Cerrada'");
        }

        existente.setCategoria(solicitudActualizada.getCategoria());
        existente.setDescripcion(solicitudActualizada.getDescripcion());
        existente.setTipo(solicitudActualizada.getTipo());

        return solicitudesRepository.save(existente);
    }

    public SolicitudesModel agregarEvidenciaASolicitud(ObjectId idSolicitud, MultipartFile archivo, String descripcion) throws IOException {
        SolicitudesModel solicitud = buscarSolicitudPorId(idSolicitud);
        
        EvidenciaEmbed nuevaEvidencia = new EvidenciaEmbed();
        nuevaEvidencia.setEvidenciaId(new ObjectId()); 
        nuevaEvidencia.setTipoArchivo(determinarTipoArchivo(archivo.getContentType()));
        nuevaEvidencia.setRutaArchivo(almacenamientoService.almacenarArchivo(archivo));
        nuevaEvidencia.setFechaHoraCarga(LocalDateTime.now());
        
        solicitud.getEvidencias().add(nuevaEvidencia);
        return solicitudesRepository.save(solicitud);
    }

    @Override
    public List<SolicitudesModel.SolicitudResumen> listarResumenesPorUsuarioId(ObjectId usuarioId) {
        return solicitudesRepository.findResumenByUsuarioId(usuarioId);
    }

    public void eliminarEvidenciaDeSolicitud(ObjectId idSolicitud, String idEvidencia) throws IOException {
        SolicitudesModel solicitud = buscarSolicitudPorId(idSolicitud);
        
        EvidenciaEmbed evidencia = solicitud.getEvidencias().stream()
            .filter(e -> e.getEvidenciaId().equals(new ObjectId(idEvidencia)))
            .findFirst()
            .orElseThrow(() -> new RecursoNoEncontradoException("Evidencia no encontrada"));
            
        almacenamientoService.eliminarArchivo(evidencia.getRutaArchivo());
        solicitud.getEvidencias().remove(evidencia);
        solicitudesRepository.save(solicitud);
    }
    private TipoArchivo determinarTipoArchivo(String contentType) {
        if (contentType == null) return TipoArchivo.documento;

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/png", "image/gif" -> TipoArchivo.imagen;
            case "video/mp4", "video/quicktime" -> TipoArchivo.video;
            case "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> TipoArchivo.documento;
            default -> TipoArchivo.documento;
        };
    }
    @Override
    public SolicitudesModel guardarSolicitudCompleta(SolicitudesModel solicitud) {
        return solicitudesRepository.save(solicitud);
    }   

@Override
public void cambiarEstadoSolicitud(ObjectId idSolicitud, EstadoSolicitud nuevoEstado, boolean reabierta) {
    SolicitudesModel solicitud = solicitudesRepository.findById(idSolicitud).orElse(null);
    if (solicitud != null) {
        solicitud.setEstado(nuevoEstado);
        solicitud.setFechaActualizacion(LocalDateTime.now());
        solicitud.setReabierta(reabierta);
        solicitudesRepository.save(solicitud);
    }
}

public boolean eliminarSolicitud(ObjectId solicitudId) {
        Optional<SolicitudesModel> solicitudOpt = solicitudesRepository.findById(solicitudId);

        if (solicitudOpt.isEmpty()) {
            return false;
        }

        SolicitudesModel solicitud = solicitudOpt.get();

        if (solicitud.getEstado() == EstadoSolicitud.Radicada &&
            (solicitud.getRespuestas() == null || solicitud.getRespuestas().isEmpty())) {
            solicitudesRepository.deleteById(solicitudId);
            return true;
        }

        return false;
    }
}