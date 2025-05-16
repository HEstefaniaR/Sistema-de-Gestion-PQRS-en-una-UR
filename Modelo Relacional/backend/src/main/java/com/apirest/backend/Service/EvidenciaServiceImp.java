package com.apirest.backend.Service;

import com.apirest.backend.Model.EvidenciaModel;
import com.apirest.backend.Model.SolicitudModel;
import com.apirest.backend.Model.TipoArchivo;
import com.apirest.backend.Repository.IEvidenciaRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvidenciaServiceImp implements IEvidenciaService {

    private final IEvidenciaRepository evidenciaRepository;

    @Autowired AlmacenamientoService almacenamientoService;

    public EvidenciaServiceImp(IEvidenciaRepository evidenciaRepository) {
        this.evidenciaRepository = evidenciaRepository;
    }

    @Override
    public EvidenciaModel guardarEvidencia(EvidenciaModel evidencia, MultipartFile archivo) throws IOException {
        TipoArchivo tipoArchivo = determinarTipoArchivo(archivo.getContentType());
        String rutaArchivo = almacenamientoService.guardarArchivo(archivo);

        evidencia.setTipo_archivo(tipoArchivo);
        evidencia.setRutaarchivo(rutaArchivo);

        return evidenciaRepository.save(evidencia);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaModel> obtenerTodasEvidencias() {
        return evidenciaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<EvidenciaModel> obtenerEvidenciaPorId(Integer id) {
        return evidenciaRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<EvidenciaModel> obtenerEvidenciasPorSolicitud(SolicitudModel solicitud) {
        return evidenciaRepository.findBySolicitud(solicitud);
    }

    @Override
    public void eliminarEvidencia(Integer id) {
        evidenciaRepository.deleteById(id);
    }

    @Override
    public EvidenciaModel actualizarEvidencia(EvidenciaModel evidenciaActualizada) {
        if (evidenciaRepository.existsById(evidenciaActualizada.getIdevidencia())) {
            return evidenciaRepository.save(evidenciaActualizada);
        }
        throw new RuntimeException("Evidencia no encontrada con ID: " + evidenciaActualizada.getIdevidencia());
    }
    
    private TipoArchivo determinarTipoArchivo(String contentType) {
        if (contentType == null) {
            return TipoArchivo.otro;
        }
    
        return switch (contentType.split("/")[0]) {
            case "image" -> TipoArchivo.imagen;
            case "video" -> TipoArchivo.video;
            case "audio" -> TipoArchivo.audio;
            case "application" -> TipoArchivo.pdf;
            default -> TipoArchivo.otro;
        };
    }
}
