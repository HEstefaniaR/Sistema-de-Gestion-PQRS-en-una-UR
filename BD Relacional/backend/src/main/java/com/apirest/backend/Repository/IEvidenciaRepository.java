package com.apirest.backend.Repository;

import com.apirest.backend.Model.EvidenciaModel;
import com.apirest.backend.Model.SolicitudModel;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IEvidenciaRepository extends JpaRepository<EvidenciaModel, Integer> {
    List<EvidenciaModel> findBySolicitud(SolicitudModel solicitud);
    
    boolean existsById(Integer idevidencia);
}
