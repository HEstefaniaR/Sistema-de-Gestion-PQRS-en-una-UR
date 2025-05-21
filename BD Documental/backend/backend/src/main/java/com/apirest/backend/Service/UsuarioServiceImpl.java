package com.apirest.backend.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.apirest.backend.Model.RolUsuario;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Repository.IUsuarioRepository;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    private IUsuarioRepository usuarioRepository;

    @Override
    public UsuarioModel guardarUsuario(UsuarioModel usuario) {
        if (usuario.getRol() == RolUsuario.anonimo) {
            usuario.setNombreCompleto("Anónimo");
            usuario.setTipoDocumento(null);
            usuario.setNumeroDocumento(null);  
            usuario.setCorreo(null);      
            usuario.setTelefono(null);        
            usuario.setDireccion(null);      
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<UsuarioModel> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public UsuarioModel buscarPorUsuario(String usuario) {
        return usuarioRepository.findByUsuario(usuario);
    }
}