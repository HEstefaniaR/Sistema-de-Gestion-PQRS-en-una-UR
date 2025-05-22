package com.apirest.backend.Service;

import java.util.List;

import org.bson.types.ObjectId;

import com.apirest.backend.Model.AdminModel;

public interface IAdminService {
    AdminModel guardarAdmin(AdminModel admin);
    List<AdminModel> listarAdmins();
    AdminModel buscarAdminPorId(ObjectId id);
    AdminModel buscarPorUsuario(String usuario);
}
