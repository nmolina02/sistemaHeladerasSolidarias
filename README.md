# Sistema de Heladeras Solidarias

> Trabajo Práctico Anual Integrador — Diseño de Sistemas (DDS) 2024

Sistema de gestión para una red de heladeras comunitarias distribuidas a lo largo del país, orientado a mejorar el acceso alimentario de personas en situación de vulnerabilidad socioeconómica. Desarrollado para la materia **Diseño de Sistemas** de la carrera de Ingeniería en Sistemas de Información (UTN).

---

## Contexto del problema

Una ONG decidió colocar heladeras en restaurantes, estaciones de transporte público y otros establecimientos de fácil acceso, para que colaboradores puedan depositar viandas y personas en situación vulnerable puedan retirarlas. El sistema resuelve tres problemáticas centrales:

- **Desabastecimiento y vencimiento:** heladeras que permanecen días sin comida o con alimentos en mal estado.
- **Desorganización de voluntarios:** los colaboradores no saben a qué heladera conviene llevar viandas ni si están activas.
- **Fallas técnicas sin atender:** dificultad para detectar y resolver problemas a tiempo.

---

## Funcionalidades implementadas

### Gestión de colaboradores
- Alta, baja y modificación de colaboradores (personas físicas y jurídicas).
- Personas físicas: nombre, apellido, fecha de nacimiento, DNI/pasaporte, medios de contacto (mail, teléfono, WhatsApp).
- Personas jurídicas: razón social, tipo (Gubernamental, ONG, Empresa, Institución), rubro, CUIT.
- Registro de usuario con validación de contraseñas seguras (verificación contra lista de contraseñas débiles, política NIST 800-63B, validación OWASP).

### Formas de colaboración
| Tipo | Persona Física | Persona Jurídica |
|------|:--------------:|:----------------:|
| Donación de dinero (única, semanal o mensual) | ✓ | ✓ |
| Donación de viandas | ✓ | — |
| Distribución de viandas entre heladeras | ✓ | — |
| Registro de personas en situación vulnerable | ✓ | — |
| Hacerse cargo de una heladera | — | ✓ |
| Ofrecer productos/servicios de reconocimiento | — | ✓ |

### Sistema de puntos y reconocimientos
Acumulación de puntos según la fórmula configurable de la ONG:
```
[PESOS_DONADOS] × 0.5  +  [VIANDAS_DISTRIBUIDAS] × 1  +  [VIANDAS_DONADAS] × 1.5
+ [TARJETAS_REPARTIDAS] × 2  +  [HELADERAS_ACTIVAS] × [MESES_ACTIVAS] × 5
```
Los puntos se canjean por productos y servicios ofrecidos por empresas asociadas, organizados por categoría (gastronomía, electrónica, hogar, etc.).

### Gestión de heladeras
- Registro con nombre, ubicación (latitud/longitud/dirección), modelo y fecha de inauguración.
- Estados: **En funcionamiento**, **En reparación** o **De baja**.
- Capacidad medida en unidades de viandas.
- Temperatura mínima y máxima configurables por el usuario (dentro de los rangos del modelo).
- Integración con API externa de geolocalización para sugerir puntos estratégicos de colocación.
- Visualización en mapa interactivo web.

### Sensores y monitoreo
- **Sensor de temperatura:** recibe lecturas periódicas cada 5 minutos vía broker MQTT; dispara alerta si la temperatura sale del rango aceptable.
- **Sensor de movimiento:** detecta intentos de fraude (apertura indebida) y genera alerta automáticamente.
- **Sensor de conexión:** detecta pérdida de señal de la heladera.

### Sistema de incidentes
- **Alertas automáticas:** temperatura fuera de rango, fraude detectado, pérdida de conexión.
- **Fallas técnicas:** reportadas manualmente por colaboradores con descripción y foto.
- Clasificación por gravedad: BAJA / MEDIA / ALTA.
- Asignación automática al técnico más cercano (búsqueda periódica cada 60 segundos).
- Registro de visitas técnicas con descripción, foto y estado de resolución.
- La heladera se marca como inactiva al generarse un incidente y vuelve a activarse al resolverse.

### Tarjetas de acceso
- **Tarjeta colaborador:** permite registrar solicitudes de apertura (válidas por 3 horas) y auditar todas las operaciones en heladeras.
- **Tarjeta persona vulnerable:** código alfanumérico único de 11 caracteres; límite de 4 usos diarios más 2 adicionales por cada menor a cargo; permite extraer una vianda por uso.

### Suscripciones y notificaciones
Los colaboradores pueden suscribirse a heladeras y ser notificados ante:
1. Quedan pocas viandas disponibles (umbral configurable por el colaborador).
2. Quedan pocos lugares libres para ingresar viandas.
3. La heladera sufrió un desperfecto y las viandas deben redistribuirse urgentemente (el sistema sugiere heladeras destino).

Canales de notificación: **Mail**, **WhatsApp** y **Telegram**.

### Carga masiva de colaboraciones
Importación de colaboraciones históricas mediante archivo `.csv` con los campos: tipo de documento, número, nombre, apellido, mail, fecha, forma de colaboración y cantidad. Si el colaborador no existe en el sistema, se crea y se le envía un mail de bienvenida con sus credenciales.

### Reportes
Generados automáticamente cada domingo a medianoche y disponibles en formato **Excel** y **PDF**:
- Cantidad de fallas por heladera.
- Cantidad de viandas retiradas/colocadas por heladera.
- Cantidad de viandas donadas por colaborador.

También se pueden solicitar reportes individuales a demanda (válidos por 48 horas).

### API REST propia (Servicio de Reconocimientos Extra)
Endpoint para que empresas externas consulten colaboradores destacados. Recibe parámetros de puntos mínimos, donaciones mínimas del último mes y cantidad máxima de resultados; devuelve la lista de colaboradores que cumplen los criterios junto con su puntaje acumulado.

### Observabilidad y seguridad
- Métricas expuestas en `/metrics` con **Micrometer** y **Prometheus**.
- SSO (Single Sign-On) integrado en el flujo de autenticación.
- Análisis de vulnerabilidades de código estático.

---

## Arquitectura

El sistema sigue una arquitectura en capas con separación clara de responsabilidades:

```
┌──────────────────────────────────────────────┐
│              REST API (Javalin)              │  ← receptorDeJSON
├──────────────────────────────────────────────┤
│          Lógica de Dominio / Casos de Uso    │  ← Heladera, persona, colaboraciones...
├──────────────────────────────────────────────┤
│          Repositorios (Data Access Layer)    │  ← repository/
├──────────────────────────────────────────────┤
│     Persistencia JPA/Hibernate + MySQL       │  ← persistencia/
└──────────────────────────────────────────────┘
```

**Patrones de diseño aplicados:**
- **Singleton** — gestores centrales (GestorIncidentes, GestorSuscripcionesHeladeras) y repositorios.
- **Factory** — creación de colaboraciones (`FactoryColaboracion`).
- **Repository** — abstracción del acceso a datos para cada entidad.
- **Observer** (implícito) — notificaciones ante cambios de estado de heladeras.
- **Strategy** (implícito) — distintas estrategias de notificación (mail, WhatsApp, Telegram).

---

## Stack tecnológico

| Categoría | Tecnología |
|-----------|-----------|
| Lenguaje | Java 17 |
| Build | Maven |
| Web framework | Javalin 3.13 |
| ORM / Persistencia | Hibernate 5.4 + JPA 2.2 |
| Base de datos | MySQL 8 |
| Conexión HTTP | OkHttp 4 / Apache HttpClient |
| JSON | Gson 2.8 |
| Reportes Excel | Apache POI 5.2 |
| Reportes PDF | iText 7 |
| Email | JavaMail (javax.mail) |
| Métricas | Micrometer + Prometheus |
| Scheduling | Quartz Scheduler 2.3 |
| Templates | Thymeleaf 3 |
| Reducción de boilerplate | Lombok |
| Variables de entorno | dotenv-java |
| Testing | JUnit 4 + Mockito 3 |
| Logging | Logback / SLF4J |

---

## Estructura del proyecto

```
src/
├── main/
│   ├── java/
│   │   ├── Heladera/                        # Entidad Heladera y subcomponentes
│   │   │   ├── controladoresHeladera/       # Controladores de operaciones
│   │   │   ├── incidente/                   # Incidentes y alertas
│   │   │   └── sensoreo/                    # Sensores de temperatura y movimiento
│   │   ├── persona/                         # Jerarquía de personas y roles
│   │   │   ├── personas/                    # PersonaFisica, PersonaJuridica
│   │   │   └── roles/                       # Colaborador, Tecnico, PersonaVulnerable
│   │   ├── colaboraciones/                  # Todas las formas de colaboración
│   │   ├── premios/                         # Sistema de puntos y premios
│   │   ├── suscripciones/                   # Suscripciones a heladeras
│   │   ├── tarjetas/                        # Tarjetas colaborador y vulnerable
│   │   ├── reportes/                        # Generación de reportes
│   │   ├── localizacion/                    # Ubicaciones y API geolocalización
│   │   ├── medioDeContacto/                 # Mail, WhatsApp, Telegram, Teléfono
│   │   ├── repository/                      # Capa de acceso a datos
│   │   ├── persistencia/                    # CRUD y utilidades de BD
│   │   ├── receptorDeJSON/Receptores/       # Servidor REST (Javalin)
│   │   ├── validador/                       # Validación de contraseñas
│   │   └── factoryColaboracion/             # Factory de colaboraciones
│   └── resources/
│       ├── META-INF/persistence.xml         # Configuración JPA/Hibernate
│       ├── logback.xml                      # Configuración de logs
│       ├── csvs/                            # Archivos CSV de carga masiva
│       └── mapaInteractivoHeladeras/        # Frontend web (HTML/CSS/JS)
└── test/
    └── java/
        ├── Tests2daEntrega.java             # Suite de tests de integración
        └── org/example/AppTest.java         # Tests unitarios
```

---

## Configuración y ejecución

### Prerrequisitos
- Java 17+
- Maven 3.6+
- MySQL 8+

### Variables de entorno

Crear un archivo `.env` en la raíz del proyecto:

```env
DB_URL=jdbc:mysql://localhost:3306/heladeras_solidarias
DB_USERNAME=tu_usuario
DB_PASSWORD=tu_contraseña
```

### Compilar y ejecutar

```bash
# Compilar y empaquetar
mvn clean package

# Ejecutar
java -jar target/validador-1.0-SNAPSHOT.jar
```

El servidor inicia en `http://localhost:4567`.  
Las métricas de Prometheus se exponen en `http://localhost:4567/metrics`.

### Ejecutar tests

```bash
mvn test
```

---

## Entregas del TP

| # | Entrega | Contenido principal |
|---|---------|---------------------|
| 1 | Modelado en Objetos Parte I | Dominio base, colaboradores, heladeras, viandas, validación de contraseñas, mapa |
| 2 | Modelado en Objetos Parte II | Sensores, técnicos, puntos/premios, carga masiva CSV, API geolocalización |
| 3 | Modelado en Objetos Parte III | Incidentes, suscripciones, notificaciones, reportes, integración broker MQTT |
| 4 | Diseño y Maquetado UI | Wireframes y maquetas HTML/CSS con Bootstrap |
| 5 | Arquitectura Web MVC | Cliente liviano con server-side rendering (Thymeleaf) |
| 6 | Persistencia y API REST | ORM con Hibernate/MySQL, servicio REST de reconocimientos |
| 7 | Despliegue | Despliegue en la nube, propuesta de separación en microservicios |
| 8 | Observabilidad y Seguridad | Micrometer/Prometheus, SSO, análisis de vulnerabilidades estático |

---

*Trabajo Práctico Anual Integrador — Diseño de Sistemas — UTN 2024*
