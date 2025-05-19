package com.apirest.backend.Service;

import com.apirest.backend.Model.EvidenciaModel;
import com.apirest.backend.Model.SolicitudModel;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

public interface IEvidenciaService {
    
    EvidenciaModel guardarEvidencia(EvidenciaModel evidencia, MultipartFile archivo) throws IOException;
    
    List<EvidenciaModel> obtenerTodasEvidencias();
    
    Optional<EvidenciaModel> obtenerEvidenciaPorId(Integer id);
    
    List<EvidenciaModel> obtenerEvidenciasPorSolicitud(SolicitudModel solicitud);
    
    void eliminarEvidencia(Integer id);
    
    EvidenciaModel actualizarEvidencia(EvidenciaModel evidenciaActualizada);
}