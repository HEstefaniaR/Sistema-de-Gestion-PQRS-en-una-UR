package com.apirest.backend.Exception;

public class RecursoNoEncontradoException extends RuntimeException {
	public RecursoNoEncontradoException(String mensaje){
		super(mensaje);
	}
}