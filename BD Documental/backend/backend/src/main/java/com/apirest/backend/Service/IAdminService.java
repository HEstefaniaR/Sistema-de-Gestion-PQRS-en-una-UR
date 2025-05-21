package com.apirest.backend.Service;

import java.util.List;
import com.apirest.backend.Model.AdminModel;

public interface IAdminService {
    AdminModel guardarAdmin(AdminModel admin);
    List<AdminModel> listarAdmins();
    AdminModel buscarAdminPorId(Integer id);
    AdminModel actualizarAdminPorId(Integer id, AdminModel admin);
    void eliminarAdminPorId(Integer id);
    AdminModel buscarPorUsuario(String usuario);
}
