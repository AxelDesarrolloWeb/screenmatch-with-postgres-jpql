package com.aluracursos.screenmatch;

import com.aluracursos.screenmatch.service.SerieCleanupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ScreenmatchApplication implements CommandLineRunner {  // Implementa la interfaz

	@Autowired
	private SerieCleanupService cleanupService;  // Inyecta el servicio

	public static void main(String[] args) {
		SpringApplication.run(ScreenmatchApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		cleanupService.eliminarSeriesInvalidas();  // Ejecuta la limpieza al iniciar
	}
}