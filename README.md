# Resumen de Aprendizajes - Spring Boot + PostgreSQL + OMDb API (Actualizado)

## Tecnologías Integradas

### 🏗️ Spring Boot Framework
- Creación de aplicaciones Java con configuración automática
- Uso de `@SpringBootApplication` para iniciar la aplicación
- Implementación de `CommandLineRunner` para ejecución al iniciar
- Spring Data JPA para operaciones avanzadas con bases de datos
- **Nuevo**: Paginación con `Pageable` y `PageRequest`

### 🐘 PostgreSQL Database
- Configuración de entidades con JPA annotations:
  - `@Entity` para mapear clases a tablas
  - `@Id` para identificar la llave primaria
  - `@GeneratedValue` para autoincrementar IDs
  - `@Column` para personalizar columnas
  - `@Enumerated` para mapear enumeraciones
  - `@OneToMany` y `@ManyToOne` para relaciones entre entidades
- **Nuevo**: Consultas JPQL con parámetros nombrados
- **Nuevo**: Manejo de relaciones en consultas personalizadas

### 🌐 OMDb API Integration
- Consumo de API RESTful para obtener datos de series y episodios
- Uso de `HttpClient` para realizar solicitudes HTTP
- Manejo de respuestas JSON con Jackson
- Gestión segura de API keys con variables de entorno
- **Corrección**: Formato correcto de URL con parámetros

### 🔄 Jackson Library
- Serialización/deserialización JSON
- Anotaciones como `@JsonAlias` y `@JsonIgnoreProperties` para mapeo de propiedades
- Manejo de formatos de fecha con `LocalDate`

## Nuevas Características Implementadas

### Búsquedas Avanzadas
```java
// Top 5 de mejores series
List<Serie> findTop5ByOrderByEvaluacionDesc();

// Búsqueda por categoría
List<Serie> findByGenero(Categoria categoria);

// Búsqueda por número de temporadas
@Query("SELECT s FROM Serie s WHERE s.totalTemporadas >= :totalTemporadas")
List<Serie> seriesPorTemporadas(Integer totalTemporadas);

// Búsqueda por evaluación mínima
@Query("SELECT s FROM Serie s WHERE s.evaluacion >= :evaluacionMinima")
List<Serie> seriesPorEvaluacion(Double evaluacionMinima);

// NUEVO: Buscar episodios por nombre
@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE e.titulo ILIKE %:nombreEpisodio%")
List<Episodio> episodioPorNombre(String nombreEpisodio);

// NUEVO: Top 5 episodios por serie
@Query("SELECT e FROM Serie s JOIN s.episodios e WHERE s.titulo = :tituloSerie ORDER BY e.evaluacion DESC")
List<Episodio> top5Episodios(String tituloSerie, Pageable pageable);
```

### Gestión de Relaciones JPA
```java
// Entidad Serie
@OneToMany(mappedBy = "serie", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
private List<Episodio> episodios = new ArrayList<>();

// Entidad Episodio
@ManyToOne
@JoinColumn(name = "serie_id")
private Serie serie;
```

### Manejo de Enums Complejos
```java
public enum Categoria {
    // Géneros con nombres en inglés y español
    ACCION("Action", "Acción"),
    AVENTURA("Adventure", "Aventura"),
    // ... otros géneros ...
    
    // Métodos de conversión
    public static Categoria fromString(String text) { ... }
    public static Categoria fromEspanol(String text) { ... }
}
```

## Mejoras y Buenas Prácticas

### 🔒 Seguridad Mejorada
- Uso de `System.getenv()` para API keys en lugar de hardcoding
- Validación de entradas del usuario para prevenir errores
- Manejo de excepciones con bloques try-catch
- **Corrección**: Formato correcto de URL de API

### 🧩 Validación de Datos
- Comprobación de valores nulos antes de operar con ellos
- Conversión segura de tipos de datos
- Manejo de errores en conversiones y parseos
- **Nuevo**: Validación de rango para evaluaciones (0-10)

### 📊 Gestión de Relaciones
- Configuración correcta de relaciones bidireccionales
- Uso de `CascadeType.ALL` para persistencia automática
- FetchType optimizado para diferentes escenarios
- **Nuevo**: Consultas JPQL con joins explícitos


## Flujo de Trabajo Actualizado

1. **Búsqueda de series**: Consulta a OMDb API y almacenamiento en PostgreSQL
2. **Obtención de episodios**: Recuperación de temporadas y episodios por serie
3. **Consultas locales**:
   - Listado completo de series
   - Búsqueda por título
   - Top 5 de mejores series
   - Filtrado por categoría
   - Búsqueda por número de temporadas
   - Filtrado por evaluación mínima
   - **NUEVO**: Búsqueda de episodios por nombre
   - **NUEVO**: Top 5 episodios por serie
4. **Persistencia de relaciones**: Almacenamiento de episodios vinculados a series

## Configuración de Proyecto

### application.properties
```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/screenmatch
spring.datasource.username=postgres
spring.datasource.password=tu_contraseña
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

### Manejo de API Keys
```java
// Uso de variables de entorno con formato correcto
private final String API_KEY = "&apikey=" + System.getenv("OMDB_APIKEY");
```

## Próximos Pasos

1. Implementar autenticación con Spring Security
2. Crear API REST para exponer los datos de series
3. Desarrollar frontend básico con Thymeleaf
4. Implementar paginación completa en los resultados
5. Agregar sistema de recomendaciones basado en géneros
6. Desplegar aplicación en la nube (Heroku, AWS)
7. Implementar pruebas unitarias y de integración
8. **Nuevo**: Añadir búsqueda de actores y personajes

```mermaid
graph TD
    A[Menú Principal] --> B[Buscar serie]
    A --> C[Buscar episodios]
    A --> D[Listar series]
    A --> E[Buscar por título]
    A --> F[Top 5 series]
    A --> G[Buscar por categoría]
    A --> H[Buscar por temporadas]
    A --> I[Buscar por evaluación]
    A --> J[NUEVO: Buscar episodios por nombre]
    A --> K[NUEVO: Top 5 episodios por serie]
    B --> L[Consulta OMDb API]
    J --> M[Consulta local con LIKE]
    L --> O[Persistir en DB]
    C --> P[Seleccionar serie]
    P --> Q[Obtener episodios]
    Q --> R[Persistir episodios]
```
