package com.matheussilvadev.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.matheussilvadev.model.Usuario;

@Repository
public interface UsuarioRepository extends CrudRepository<Usuario, Long>{

}
