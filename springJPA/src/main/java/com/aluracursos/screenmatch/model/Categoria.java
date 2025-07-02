package com.aluracursos.screenmatch.model;

public enum Categoria {
    // Géneros principales
    ACCION("Action", "Acción"),
    AVENTURA("Adventure", "Aventura"),
    ANIMACION("Animation", "Animación"),
    COMEDIA("Comedy", "Comedia"),
    CRIMEN("Crime", "Crimen"),
    DOCUMENTAL("Documentary", "Documental"),
    DRAMA("Drama", "Drama"),
    FAMILIA("Family", "Familia"),
    FANTASIA("Fantasy", "Fantasía"),
    HISTORICO("History", "Histórico"),
    TERROR("Horror", "Terror"),
    MUSICAL("Musical", "Musical"),
    MISTERIO("Mystery", "Misterio"),
    ROMANCE("Romance", "Romance"),
    CIENCIA_FICCION("Sci-Fi", "Ciencia Ficción"),
    THRILLER("Thriller", "Thriller"),
    BELICO("War", "Bélico"),
    WESTERN("Western", "Western"),

    // Géneros secundarios
    ANIME("Anime", "Anime"),
    BIOGRAFIA("Biography", "Biografía"),
    DEPORTES("Sport", "Deportes"),
    REALITY("Reality TV", "Reality"),
    MUSICA("Music", "Música"),
    GAME_SHOW("Game Show", "Programa de concursos"),
    CORTOMETRAJE("Short", "Cortometraje"),
    LIFESTYLE("Lifestyle", "Estilo de vida"),
    SUPERNATURAL("Supernatural", "Supernatural"),
    FICCION("Fiction", "Ficción"),

    // Nuevas categorías agregadas
    FANTASIA_CIENTIFICA("Science Fantasy", "Fantasía Científica"),
    CRIMEN_POLICIACO("Crime, Police", "Crimen Policiaco"),
    AVENTURA_FAMILIAR("Family Adventure", "Aventura Familiar"),
    DRAMA_HISTORICO("Historical Drama", "Drama Histórico"),
    COMEDIA_DRAMATICA("Comedy-Drama", "Comedia Dramática"),
    SUSPENSO_PSICOLOGICO("Psychological Thriller", "Suspenso Psicológico"),
    AVENTURA_ANIMADA("Animated Adventure", "Aventura Animada"),
    CIENCIA_FICCION_DISTOPICA("Dystopian Sci-Fi", "Ciencia Ficción Distópica"),
    ACCION_AVENTURA("Action-Adventure", "Acción-Aventura"),
    DRAMA_POLITICO("Political Drama", "Drama Político");

    private final String categoriaOmdb;
    private final String categoriaEspanol;

    // Constructor para ambos idiomas
    Categoria(String categoriaOmdb, String categoriaEspanol) {
        this.categoriaOmdb = categoriaOmdb;
        this.categoriaEspanol = categoriaEspanol;
    }

    // Constructor sobrecargado para compatibilidad
    Categoria(String categoriaOmdb) {
        this(categoriaOmdb, categoriaOmdb); // Usa el mismo nombre en español por defecto
    }

    public static Categoria fromString(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String primerGenero = text.split(",")[0].trim();

        // Primera pasada: búsqueda exacta
        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaOmdb.equalsIgnoreCase(primerGenero)) {
                return categoria;
            }
        }

        // Segunda pasada: búsqueda flexible
        for (Categoria categoria : Categoria.values()) {
            if (primerGenero.equalsIgnoreCase(categoria.categoriaOmdb) ||
                    primerGenero.equalsIgnoreCase(categoria.categoriaEspanol)) {
                return categoria;
            }
        }

        return DRAMA; // Valor por defecto
    }

    public static Categoria fromEspanol(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }

        String primerGenero = text.split(",")[0].trim();

        for (Categoria categoria : Categoria.values()) {
            if (categoria.categoriaEspanol.equalsIgnoreCase(primerGenero)) {
                return categoria;
            }
        }
        return DRAMA;
    }

    // Método para obtener la versión en español
    public String getCategoriaEspanol() {
        return categoriaEspanol;
    }
}