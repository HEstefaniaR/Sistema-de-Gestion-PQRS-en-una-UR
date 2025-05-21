package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.Model.UsuarioModel;

public interface IUsuarioService {
    UsuarioModel guardarUsuario(UsuarioModel usuario);
    List<UsuarioModel> listarUsuarios();
    UsuarioModel buscarPorUsuario(String usuario);
}
