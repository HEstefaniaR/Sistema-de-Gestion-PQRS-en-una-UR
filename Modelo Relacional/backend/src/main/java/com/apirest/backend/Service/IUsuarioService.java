package com.apirest.backend.Service;

import java.util.List;

import com.apirest.backend.Model.UsuarioModel;

public interface IUsuarioService {
    // Declaramos todas las operaciones CRUD
    public UsuarioModel registrarUsuario(UsuarioModel usuario);
    public List<UsuarioModel> listarUsuarios();
    UsuarioModel buscarPorUsuario(String usuario);
  
    //Otras operaciones CRUD o consultas
    public UsuarioModel buscarUsuarioPorId(Integer id);
    public void eliminarUsuarioPorId(Integer id);
    public UsuarioModel actualizarUsuarioPorId(Integer id, UsuarioModel usuario);
    
}
