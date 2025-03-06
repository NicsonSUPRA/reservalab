package com.uespi.reservalab.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.uespi.reservalab.annotations.ClientDefault;
import com.uespi.reservalab.dto.ClientDTO;
import com.uespi.reservalab.models.Client;
import com.uespi.reservalab.services.ClientService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    private final PasswordEncoder passwordEncoder;

    @ClientDefault
    private final Client clientDefault;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrarClient(@RequestBody ClientDTO clientDTO) throws Exception {
        if (clientService.obterClientByClientId(clientDTO.getClientId()) != null) {
            throw new Exception("Client já cadastrado");
        }
        clientDefault.setClientId(clientDTO.getClientId());
        clientDefault.setClientSecret(passwordEncoder.encode(clientDTO.getClientSecret()));
        clientService.salvarClient(clientDefault);
    }

    @GetMapping
    public ResponseEntity<List<Client>> findAllClient() {
        return ResponseEntity.ok(clientService.findAllClient());
    }

}
