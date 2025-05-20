package com.apirest.backend.Service;

import java.util.List;
import com.apirest.backend.Model.AdminModel;

public interface IAdminService {
    AdminModel guardarAdmin(AdminModel admin);
    List<AdminModel> listarAdmins();
    AdminModel buscarAdminPorId(String id);
    AdminModel actualizarAdminPorId(String id, AdminModel admin);
    void eliminarAdminPorId(String id);
}
