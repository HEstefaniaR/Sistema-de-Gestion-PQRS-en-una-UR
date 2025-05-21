package com.apirest.backend.Service;

import com.apirest.backend.Model.EvidenciaModel;
import com.apirest.backend.Model.TipoArchivo;
import com.apirest.backend.Repository.IEvidenciaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class IEvidenciaService {

    @Autowired
    private IEvidenciaRepository evidenciaRepository;

    @Autowired
    private AlmacenamientoService almacenamientoService;

    public EvidenciaModel guardarEvidenciaConArchivo(EvidenciaModel evidencia, MultipartFile archivo) throws IOException {
        String nombreArchivo = almacenamientoService.almacenarArchivo(archivo);
        evidencia.setRutaArchivo(nombreArchivo);
        evidencia.setTipoArchivo(determinarTipoArchivo(archivo.getContentType()));
        evidencia.setFechaHoraCarga(LocalDateTime.now());
        return evidenciaRepository.save(evidencia);
    }

    private TipoArchivo determinarTipoArchivo(String contentType) {
        if (contentType == null) return TipoArchivo.OTRO;
        
        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/png", "image/gif" -> TipoArchivo.IMAGEN;
            case "application/pdf" -> TipoArchivo.PDF;
            case "application/msword", 
                 "application/vnd.openxmlformats-officedocument.wordprocessingml.document" -> TipoArchivo.DOCUMENTO;
            case "video/mp4", "video/quicktime" -> TipoArchivo.VIDEO;
            case "audio/mpeg", "audio/wav" -> TipoArchivo.AUDIO;
            default -> TipoArchivo.OTRO;
        };
    }

    public void eliminarEvidenciaCompleta(String idEvidencia) throws IOException {
        EvidenciaModel evidencia = evidenciaRepository.findById(idEvidencia)
            .orElseThrow(() -> new RuntimeException("Evidencia no encontrada"));
        almacenamientoService.eliminarArchivo(evidencia.getRutaArchivo());
        evidenciaRepository.deleteById(idEvidencia);
    }
}