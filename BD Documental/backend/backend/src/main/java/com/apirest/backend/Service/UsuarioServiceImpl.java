package com.apirest.backend.Service;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Repository.IUsuarioRepository;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public String guardarUsuario(UsuarioModel usuario) {
        usuarioRepository.save(usuario);
        return "Usuario guardado correctamente.";
    }

    @Override
    public List<UsuarioModel> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioModel buscarUsuarioPorId(ObjectId id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Override
    public UsuarioModel actualizarUsuario(ObjectId id, UsuarioModel usuario) {
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }

    @Override
    public void eliminarUsuario(ObjectId id) {
        usuarioRepository.deleteById(id);
    }
}
