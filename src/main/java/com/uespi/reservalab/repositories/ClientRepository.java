package com.uespi.reservalab.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.uespi.reservalab.models.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, UUID> {
    public Client findByClientId(String clientId);
}
