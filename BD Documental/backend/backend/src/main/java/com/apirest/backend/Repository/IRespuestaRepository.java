package com.apirest.backend.Repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import com.apirest.backend.Model.RespuestaModel;

public interface IRespuestaRepository extends MongoRepository<RespuestaModel, ObjectId> {}
