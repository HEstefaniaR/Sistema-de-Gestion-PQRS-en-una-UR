package com.apirest.backend.Service;
import java.io.IOException; 
import java.time.LocalDateTime;
import java.util.List;

import org.bson.types.ObjectId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.apirest.backend.DTO.SolicitudesDTO;
import com.apirest.backend.Exception.RecursoNoEncontradoException;
import com.apirest.backend.Model.EstadoSolicitud;
import com.apirest.backend.Model.SolicitudesModel;
import com.apirest.backend.Model.SolicitudesModel.EvidenciaEmbed;
import com.apirest.backend.Model.TipoArchivo;
import com.apirest.backend.Repository.ISolicitudesRepository;

@Service
public class SolicitudesService implements ISolicitudesService {

    @Autowired 
    private ISolicitudesRepository solicitudesRepository;
    
    @Autowired
    private AlmacenamientoService almacenamientoService;

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

    public SolicitudesModel buscarSolicitudConEvidencias(Integer id) {
        return solicitudesRepository.findSolicitudConEvidencias(id);
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

    public SolicitudesModel agregarEvidenciaASolicitud(Integer idSolicitud, MultipartFile archivo, String descripcion) throws IOException {
        SolicitudesModel solicitud = buscarSolicitudPorId(idSolicitud);
        
        EvidenciaEmbed nuevaEvidencia = new EvidenciaEmbed();
        nuevaEvidencia.setIdEvidencia(new ObjectId()); 
        nuevaEvidencia.setTipoArchivo(determinarTipoArchivo(archivo.getContentType()));
        nuevaEvidencia.setRutaArchivo(almacenamientoService.almacenarArchivo(archivo));
        nuevaEvidencia.setFechaHoraCarga(LocalDateTime.now());
        
        solicitud.getEvidencias().add(nuevaEvidencia);
        return solicitudesRepository.save(solicitud);
    }

    @Override
    public List<SolicitudesDTO> listarPorUsuarioId(Integer usuarioId) {
        return solicitudesRepository.findByUsuarioId(usuarioId);
    }

    public void eliminarEvidenciaDeSolicitud(Integer idSolicitud, String idEvidencia) throws IOException {
        SolicitudesModel solicitud = buscarSolicitudPorId(idSolicitud);
        
        EvidenciaEmbed evidencia = solicitud.getEvidencias().stream()
            .filter(e -> e.getIdEvidencia().equals(new ObjectId(idEvidencia)))
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
}