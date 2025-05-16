package com.apirest.backend.Repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.apirest.backend.Model.UsuarioModel;
import com.apirest.backend.Model.RolUsuario;

public interface IUsuarioRepository extends JpaRepository<UsuarioModel, Integer> {

    // Retornar todos los usuarios según su rol
    List<UsuarioModel> findByRol(RolUsuario rol);

    // Retornar usuario por número de documento
    UsuarioModel findByNumeroDocumento(String numeroDocumento);

    // Consulta nativa para obtener usuarios por su rol
    @Query(value = "SELECT u.id_usuario, u.nombre, u.correo, u.rol " +
                   "FROM usuarios AS u WHERE u.rol = :rol", nativeQuery = true)
    List<Object[]> listarUsuariosPorRol(@Param("rol") String rol);

    UsuarioModel findByUsuario(String usuario);
}