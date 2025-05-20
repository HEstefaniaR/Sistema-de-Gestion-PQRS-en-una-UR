package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.Model.UsuarioModel;

public interface IUsuarioService {
    public String guardarUsuario(UsuarioModel usuario);
    public List<UsuarioModel> listarUsuarios();
}
