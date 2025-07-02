package com.aluracursos.screenmatch.principal;

import com.aluracursos.screenmatch.model.*;
import com.aluracursos.screenmatch.repository.SerieRepository;
import com.aluracursos.screenmatch.service.ConsumoAPI;
import com.aluracursos.screenmatch.service.ConvierteDatos;

import org.springframework.data.domain.PageRequest;

import java.awt.print.Pageable;
import java.util.*;
import java.util.stream.Collectors;

public class Principal {
    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://www.omdbapi.com/?t=";
    // Obtener la API key de las variables de entorno
    private final String API_KEY = System.getenv("OMDB_APIKEY");
    private ConvierteDatos conversor = new ConvierteDatos();
    private List<DatosSerie> datosSeries = new ArrayList<>();
    private SerieRepository repository;
    private List<Serie> series;
    private Optional<Serie> serieBuscada;

    public Principal(SerieRepository repository) {
        this.repository = repository;
    }

    public void muestraElMenu() {
        var opcion = -1;
        while (opcion != 0) {
            var menu = """
                    1 - Buscar series 
                    2 - Buscar episodios
                    3 - Listar series buscadas
                    4 - Buscar series por título
                    5 - Top 5 mejores series
                    6 - Buscar series por categoría
                    7 - Buscar series por cantidad de temporadas
                    8 - Buscar series por número de evaluación
                    9 - Buscar episodios por su nombre
                    10 - Buscar top 5 episodios
                    0 - Salir
                    """;
            System.out.println(menu);

            opcion = teclado.nextInt();
            teclado.nextLine();

            switch (opcion) {
                case 1:
                    buscarSerieWeb();
                    break;
                case 2:
                    buscarEpisodioPorSerie();
                    break;
                case 3:
                    listarSeriesBuscadas();
                    break;
                case 4:
                    buscarSeriesPorTitulo();
                    break;
                case 5:
                    buscarTop5Series();
                    break;
                case 6:
                    buscarSeriesPorCategoria();
                    break;
                case 7:
                    buscarSeriesPorCantTemporadas();
                    break;
                case 8:
                    buscarSeriesPorNumEvaluacion();
                    break;
                case 9:
                    buscarEpisodioPorNombre();
                    break;
                case 10:
                    buscarTop5Episodios();
                    break;
                case 0:
                    System.out.println("Cerrando la aplicación...");
                    break;
                default:
                    System.out.println("Opción inválida");
            }
        }
    }

    private void buscarSerieWeb() {
        DatosSerie datos = getDatosSerie();
        if (datos.titulo() == null || datos.titulo().isBlank()) {
            System.out.println("Error: Serie no encontrada o datos inválidos");
            return;
        }
        Serie serie = new Serie(datos);
        repository.save(serie);
        System.out.println("Serie guardada: " + datos.titulo());
    }

    private DatosSerie getDatosSerie() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        var json = consumoApi.obtenerDatos(URL_BASE + nombreSerie.replace(" ", "+") + API_KEY);
        DatosSerie datos = conversor.obtenerDatos(json, DatosSerie.class);
        System.out.println(datos.sinopsis());
        return datos;
    }

    private void buscarEpisodioPorSerie() {
        listarSeriesBuscadas();
        System.out.println("Escribe el nombre de una serie para ver sus episodios");
        var nombreSerie = teclado.nextLine();

        Optional<Serie> serie = series.stream()
                .filter(s -> s.getTitulo() != null)
                .filter(s -> s.getTitulo().toLowerCase().contains(nombreSerie.toLowerCase()))
                .findFirst();

        if (serie.isPresent()) {
            var serieEncontrada = serie.get();
            System.out.println("Buscando episodios de: " + serieEncontrada.getTitulo());

            List<DatosTemporadas> temporadas = new ArrayList<>();

            for (int i = 1; i <= serieEncontrada.getTotalTemporadas(); i++) {
                var json = consumoApi.obtenerDatos(URL_BASE + serieEncontrada.getTitulo().replace(" ", "+") + "&season=" + i + API_KEY);
                DatosTemporadas datosTemporada = conversor.obtenerDatos(json, DatosTemporadas.class);
                temporadas.add(datosTemporada);
            }
            temporadas.forEach(System.out::println);

            List<Episodio> episodios = temporadas.stream()
                    .flatMap(d -> d.episodios().stream()
                            .map(e -> new Episodio(d.numero(), e)))
                    .collect(Collectors.toList());

            serieEncontrada.setEpisodios(episodios);
            repository.save(serieEncontrada);
            System.out.println("Episodios guardados: " + episodios.size());
        } else {
            System.out.println("Serie no encontrada en la base de datos local");
        }
    }

    private void listarSeriesBuscadas() {
        series = repository.findAll();

        long invalidCount = series.stream()
                .filter(s -> s.getTitulo() == null)
                .count();

        if (invalidCount > 0) {
            System.out.println("\nAdvertencia: " + invalidCount +
                    " series inválidas no se mostrarán");
        }

        series.stream()
                .filter(s -> s.getTitulo() != null) // Filtra para mostrar
                .sorted(Comparator.comparing(
                        Serie::getGenero,
                        Comparator.nullsFirst(Comparator.naturalOrder())
                ))
                .forEach(System.out::println);
    }

    private void buscarSeriesPorTitulo() {
        System.out.println("Escribe el nombre de la serie que deseas buscar");
        var nombreSerie = teclado.nextLine();
        serieBuscada = repository.findByTituloContainsIgnoreCase(nombreSerie);

        if (serieBuscada.isPresent()) {
            System.out.println("La serie buscada es: " + serieBuscada.get());
        } else {
            System.out.println("Serie no encontrada");
        }
    }

    private void buscarTop5Series() {
        System.out.println("Buscando el top 5 de las mejores series...");
        List<Serie> topSeries = repository.findTop5ByOrderByEvaluacionDesc();
        topSeries.forEach(s ->
                System.out.println("Serie :" + s.getTitulo() + "Evaluación: " + s.getEvaluacion()));
    }

    private void buscarSeriesPorCategoria() {
        System.out.println("Escriba el género/categoría de la serie que desea buscar");
        var genero = teclado.nextLine();
        var categoria = Categoria.fromEspanol(genero);

        if (categoria == null) {
            System.out.println("Categoría no válida");
            return;
        }

        List<Serie> seriesPorCategoria = repository.findByGenero(categoria);
        System.out.println("Las series de la categoría " + genero);
        seriesPorCategoria.forEach(System.out::println);
    }

    private void buscarSeriesPorCantTemporadas() {
        System.out.println("Ingrese una cantidad mínima de temporadas");
        try {
            int cantidadTemporadas = teclado.nextInt();
            teclado.nextLine();  // Limpiar buffer

            List<Serie> series = repository.seriesPorTemporadas(cantidadTemporadas);
            System.out.println("Las series con " + cantidadTemporadas + " o más temporadas:");
            series.forEach(System.out::println);
        } catch (InputMismatchException e) {
            System.out.println("Debe ingresar un número entero");
            teclado.nextLine();  // Limpiar entrada inválida
        }
    }

    private void buscarSeriesPorNumEvaluacion() {
        System.out.println("Ingrese una evaluación mínima (0,0 - 10,0)");
        try {
            double evaluacionMinima = teclado.nextDouble();
            teclado.nextLine();  // Limpiar buffer

            if (evaluacionMinima < 0 || evaluacionMinima > 10) {
                System.out.println("La evaluación debe estar entre 0 y 10");
                return;
            }

            List<Serie> series = repository.seriesPorEvaluacion(evaluacionMinima);
            System.out.println("Las series con evaluación de " + evaluacionMinima + " o más:");
            series.forEach(System.out::println);
        } catch (InputMismatchException e) {
            System.out.println("Debe ingresar un número válido");
            teclado.nextLine();  // Limpiar entrada inválida
        }
    }

    private void  buscarEpisodioPorNombre(){
        System.out.println("Escribe el nombre del episodio que deseas buscar y devuelve su serie + otros datos");
        var nombreEpisodio = teclado.nextLine();
        List<Episodio> episodiosEncontrados = repository.episodioPorNombre(nombreEpisodio);
        episodiosEncontrados.forEach(e ->
                System.out.printf("Serie: %s Temporada %s Episodio %s Evaluación %s\n",
                        e.getSerie().getTitulo(), e.getTemporada(), e.getNumeroEpisodio(), e.getEvaluacion()));

    }

    private void buscarTop5Episodios(){
        buscarSeriesPorTitulo();
        if(serieBuscada.isPresent()){
            Serie serie = serieBuscada.get();
            List<Episodio> topEpisodios = repository.top5Episodios(serie);
            topEpisodios.forEach(e ->
                    System.out.printf("Serie: %s - Temporada %s - Episodio %s - Evaluación %s\n",
                            e.getSerie().getTitulo(), e.getTemporada(), e.getTitulo(), e.getEvaluacion()));

        }
    }
}