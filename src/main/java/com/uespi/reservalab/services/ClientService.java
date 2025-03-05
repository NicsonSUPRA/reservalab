package com.uespi.reservalab.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.uespi.reservalab.models.Client;
import com.uespi.reservalab.repositories.ClientRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ClientService {

    public final ClientRepository clientRepository;

    public Client obterClientByClientId(String clientId) {
        return clientRepository.findByClientId(clientId);
    }

    public void salvarClient(Client client) {
        clientRepository.save(client);
    }

    public List<Client> findAllClient() {
        return clientRepository.findAll();
    }

}
