package com.apirest.backend.Repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.apirest.backend.Model.UsuarioModel;

public interface IUsuarioRepository extends MongoRepository<UsuarioModel, Integer> {
    List<UsuarioModel> findByRol(String rol);

    UsuarioModel findByNumeroDocumento(String numeroDocumento);

    UsuarioModel findByUsuario(String usuario);
}
