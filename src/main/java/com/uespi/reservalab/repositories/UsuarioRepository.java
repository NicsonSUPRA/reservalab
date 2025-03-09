package com.uespi.reservalab.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    public Usuario findByLogin(String login);

    public boolean existsByLogin(String login);
}
