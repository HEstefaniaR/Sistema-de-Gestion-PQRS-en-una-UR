package com.apirest.backend.Repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.apirest.backend.Model.AdminModel;

public interface IAdminRepository extends MongoRepository<AdminModel, String> {
}
