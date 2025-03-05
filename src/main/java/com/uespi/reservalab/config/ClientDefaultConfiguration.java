package com.uespi.reservalab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.uespi.reservalab.models.Client;

@Configuration
public class ClientDefaultConfiguration {

    @Bean(name = "clientDefault")
    @Primary
    public Client clientDefault() {
        Client client = new Client();
        client.setRedirectURI("http://localhost:8080/oauth2/authorized");
        client.setScope("USUARIO");
        return client;
    }

}
