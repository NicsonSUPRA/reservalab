package com.uespi.reservalab.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Usuario;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    public Usuario findByLogin(String login);

    public boolean existsByLogin(String login);

    @Query("Select u from Usuario u where lower(u.nome) like lower(:nome)")
    public List<Usuario> obterUsuarioComNomeSemelhante(@Param("nome") String nome);

    List<Usuario> findByRolesIn(List<String> roles);
}
