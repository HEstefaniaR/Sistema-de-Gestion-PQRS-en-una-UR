package com.apirest.backend.Service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.apirest.backend.Exception.RecursoNoEncontradoException;
import com.apirest.backend.Model.RolUsuario;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Repository.IUsuarioRepository;

@Service
public class UsuarioServiceImp implements IUsuarioService {
    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UsuarioModel registrarUsuario(UsuarioModel usuario) {
        if (usuario.getRol() == RolUsuario.anonimo) {
            usuario.setNombreCompleto("Anónimo");
            usuario.setTipoDocumento(null);
            usuario.setNumeroDocumento(null);
            usuario.setCorreoElectronico(null);
            usuario.setTelefono(null);
            usuario.setDireccionInterna(null);
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioModel> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioModel buscarUsuarioPorId(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Error! El usuario con id " + id + " no fue encontrado."));
    }

    @Override
    public void eliminarUsuarioPorId(Integer id) {
        UsuarioModel usuarioExistente = buscarUsuarioPorId(id);
        usuarioRepository.delete(usuarioExistente);
    }

    @Override
    public UsuarioModel actualizarUsuarioPorId(Integer id, UsuarioModel usuario) {
        UsuarioModel usuarioExistente = buscarUsuarioPorId(id);
        usuarioExistente.setNombreCompleto(usuario.getNombreCompleto());
        usuarioExistente.setTipoDocumento(usuario.getTipoDocumento());
        usuarioExistente.setNumeroDocumento(usuario.getNumeroDocumento());
        usuarioExistente.setCorreoElectronico(usuario.getCorreoElectronico());
        usuarioExistente.setTelefono(usuario.getTelefono());
        usuarioExistente.setDireccionInterna(usuario.getDireccionInterna());
        usuarioExistente.setRol(usuario.getRol());
        return usuarioRepository.save(usuarioExistente);
    }
    @Override
    public UsuarioModel buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
}
}