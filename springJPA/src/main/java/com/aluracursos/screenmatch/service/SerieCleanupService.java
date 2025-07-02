package com.aluracursos.screenmatch.service;

import com.aluracursos.screenmatch.model.Serie;
import com.aluracursos.screenmatch.repository.SerieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SerieCleanupService {

    @Autowired
    private SerieRepository serieRepository;

    @Transactional
    public void eliminarSeriesInvalidas() {
        // Usa el nuevo método con consulta personalizada
        List<Serie> seriesInvalidas = serieRepository.findInvalidSeries();

        if (!seriesInvalidas.isEmpty()) {
            System.out.println("Eliminando " + seriesInvalidas.size() + " series inválidas...");
            serieRepository.deleteAll(seriesInvalidas);
        }
    }
}