package com.uespi.reservalab;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.uespi.reservalab.models.Laboratorio;
import com.uespi.reservalab.repositories.LaboratorioRepository;

import lombok.RequiredArgsConstructor;

@SpringBootTest
@RequiredArgsConstructor
class ReservalabApplicationTests {

	private final LaboratorioRepository laboratorioRepository;

	@Test
	void contextLoads() {

		Laboratorio laboratorio = new Laboratorio();
		laboratorio.setNome("Laboratório de Informática");
		laboratorioRepository.save(laboratorio);
	}

}
