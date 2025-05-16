package com.apirest.backend.Service;

import java.util.List;
import com.apirest.backend.Model.AdminModel;

public interface IAdminService {
    public AdminModel guardarAdmin(AdminModel admin);
    public List<AdminModel> listarAdmins();
    public AdminModel buscarAdminPorId(Integer id);
    public AdminModel actualizarAdminPorId(Integer id, AdminModel adminActualizado);
    public void eliminarAdminPorId(Integer id);
    public AdminModel buscarPorUsuario(String usuario);
}