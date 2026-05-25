# Tech-Park UQ — Sistema de Gestión de Parque de Atracciones Inteligente

> **Proyecto académico — Estructura de Datos | Universidad del Quindío**  
> Sistema integral de gestión operacional para un parque de atracciones, construido sobre estructuras de datos implementadas desde cero en Java, con API REST en Spring Boot y frontend web en Next.js.

---

##  Tabla de Contenidos

1. [Descripción General](#-descripción-general)
2. [Características Principales](#-características-principales)
3. [Estructuras de Datos Implementadas](#-estructuras-de-datos-implementadas)
4. [Tecnologías Utilizadas](#-tecnologías-utilizadas)
5. [Requisitos del Sistema](#-requisitos-del-sistema)
6. [Instalación y Ejecución](#-instalación-y-ejecución)
7. [Pruebas Unitarias](#-pruebas-unitarias)
8. [Roles del Sistema](#-roles-del-sistema)
9. [Algoritmos Importantes](#-algoritmos-importantes)
10. [API REST — Endpoints Principales](#-api-rest--endpoints-principales)
11. [Carga de Datos Iniciales](#-carga-de-datos-iniciales)
12. [Autores](#-autores)

---

##  Descripción General

**Tech-Park UQ** es una plataforma de gestión operacional completa para el parque de atracciones ficticio *Tech-Park UQ*. El sistema aborda los problemas reales de operación de un parque moderno: control de acceso, largas filas, fallos en protocolos de seguridad, asignación de personal y gestión de mantenimiento.

### Problema que resuelve

El parque operaba de forma manual, generando:
- Largas filas sin diferenciación de prioridad
- Ausencia de control de mantenimiento preventivo
- Sin mecanismo de respuesta ante emergencias climáticas
- Gestión de personal sin trazabilidad
- Sin reportes operativos al cierre de jornada

### Objetivo

Construir un sistema backend robusto usando **estructuras de datos propias** (grafo, cola de prioridad, árbol BST, lista enlazada, set) como eje central de la solución, exponiendo la lógica mediante una **API REST con Spring Boot** y consumiéndola desde un **frontend web en Next.js**.

### Enfoque académico

Cada decisión de estructura de datos está justificada por el problema que resuelve:
- Las filas virtuales con prioridad usan un **heap mínimo** propio
- El mapa físico del parque es un **grafo con lista de adyacencia** propia
- Las búsquedas de atracciones y usuarios usan un **árbol BST** propio
- El historial de visitas y los operadores de una zona usan **listas enlazadas** propias
- Los favoritos del visitante usan un **Set basado en BST** propio

---

## Características Principales

### Gestión de Visitantes
- Registro de visitantes con perfil completo (nombre, documento, edad, estatura, saldo virtual, foto opcional)
- Control de saldo virtual para atracciones con costo adicional
- Historial de visitas por visitante (lista enlazada propia)
- Registro de atracciones favoritas (Set propio basado en BST)
- Recepción de notificaciones en tiempo real (shows, clima, mantenimiento, incidentes)

### Sistema de Tickets
- Tres tipos: **GENERAL**, **FAMILIAR** y **FAST_PASS** (acceso prioritario)
- Control de estado del ticket: ACTIVO → USADO / EXPIRADO
- Expiración automática al cierre de jornada (18:00)
- Verificación de aforo del parque y de la zona antes de vender

### Filas Virtuales con Prioridad
- Cada atracción tiene su propia cola de prioridad (heap mínimo propio)
- **Fast-Pass** siempre tiene prioridad sobre tickets General y Familiar
- Dentro de la misma prioridad, el orden es por hora de llegada
- El visitante recibe notificación con su posición aproximada en la cola
- El operador llama al siguiente visitante con un solo endpoint

### Rutas Inteligentes en el Mapa
- El parque se representa como un **grafo no dirigido con pesos** (metros de distancia entre atracciones)
- El visitante puede calcular la **ruta óptima** entre su ubicación y cualquier atracción
- Algoritmo **Dijkstra** para ruta más corta con pesos reales
- Algoritmo **BFS** para recorrido por amplitud y análisis de conectividad
- Visualización interactiva del mapa en el frontend

### Mantenimiento Preventivo Automatizado
- Toda atracción se bloquea automáticamente al alcanzar **500 visitantes acumulados**
- Se genera una `AlertaMantenimiento` que ingresa a una **cola de prioridad de alertas**
- El operador registra una revisión técnica: si el resultado es SATISFACTORIA, el contador se reinicia y la atracción vuelve al estado ACTIVA
- Las alertas pendientes son visibles en el panel de administración

### Simulación Climática
- El administrador puede activar alertas de **TORMENTA_ELECTRICA** o **LLUVIA_FUERTE**
- Las atracciones de tipo **ACUATICA** y **MECANICA_ALTURA** se cierran automáticamente
- Se notifica a todos los visitantes con ticket activo y a quienes estén en cola de las atracciones afectadas
- El administrador puede finalizar la alerta para reabrir las atracciones

### Gestión de Shows
- Dos shows fijos diarios: *Show del Café* a las 10:00 y 15:00
- Estado automático: PROGRAMADO → EN_CURSO → FINALIZADO según la hora del sistema
- Aviso automático 10 minutos antes del inicio
- Notificación a todos los visitantes con ticket activo

### Gestión de Personal
- El administrador crea y asigna operadores a zonas
- Un operador sólo puede gestionar atracciones de su zona asignada
- El sistema impide que una zona quede sin operador responsable
- Los operadores se listan por zona usando lista enlazada propia

### Reportes de Jornada
Al cierre, el administrador genera un reporte con:
- Ingresos diarios totales
- Atracciones más visitadas (ordenadas por contador)
- Tiempos promedio de espera por atracción
- Cierres por clima (suma de atracciones afectadas)
- Alertas de mantenimiento del día
- Atracciones con más incidentes operativos

### Carga de Datos
- Escenario inicial hardcodeado en `DatosIniciales.java` (8 atracciones, zonas, operadores, visitantes precargados)
- Carga desde archivo CSV con formato documentado (zonas, atracciones, usuarios, senderos, tickets, shows, alertas)
- Botón de carga en el panel de administración del frontend

---

##  Estructuras de Datos Implementadas

Todas las estructuras son **implementaciones propias en Java**, sin usar `java.util.LinkedList`, `java.util.PriorityQueue` ni `java.util.TreeMap`.

---

### 1. `ListaEnlazada<T>` — Lista Enlazada Simple

**Paquete:** `techpark.estructuras.lista`

**Implementación:**  
Nodos enlazados con referencia al siguiente (`NodoLista<T>`). Soporta inserción al inicio e inserción al final. Implementa `Iterable<T>` para compatibilidad con el for-each de Java.

**Dónde se usa:**
| Uso | Razón |
|-----|-------|
| `Visitante.historialVisitas` | El historial crece dinámicamente; se inserta al inicio para mostrar el más reciente primero |
| `Zona.operadoresAsignados` | Lista dinámica de operadores; soporta agregar y eliminar con validación de mínimo 1 |
| `Atraccion.operadoresResponsables` | Operadores asignados a una atracción específica |
| `NodoGrafo.vecinos` | Lista de adyacencia del grafo (aristas de cada nodo) |

**Por qué lista enlazada y no ArrayList:**  
El tamaño es dinámico y la inserción/eliminación es frecuente. No se necesita acceso por índice aleatorio.

---

### 2. `ColaPrioridad<T extends Comparable<T>>` — Min-Heap

**Paquete:** `techpark.estructuras.cola`

**Implementación:**  
Heap mínimo sobre un `ArrayList` interno. Operaciones `subir()` y `bajar()` mantienen la propiedad del heap. 

**Dónde se usa:**
| Uso | Razón |
|-----|-------|
| `Atraccion.colaVirtual` | Cada atracción tiene su fila virtual; Fast-Pass (prioridad 1) siempre sale antes que General (prioridad 2) |
| `ServicioAlertas.colaMantenimiento` | Las alertas de mantenimiento se atienden en orden cronológico |

**Mecanismo de prioridad:**  
`EntradaEnCola.compareTo()` compara primero por prioridad del ticket (1 = Fast-Pass, 2 = General/Familiar) y en segundo lugar por hora de ingreso a la cola, garantizando FIFO dentro del mismo tipo.

---

### 3. `ArbolBST<T>` — Árbol Binario de Búsqueda

**Paquete:** `techpark.estructuras.arbol`

**Implementación:**  
BST clásico con clave `String` (comparación `compareToIgnoreCase`). Soporta `insertar`, `buscar`, `contiene`, e `inorden` (recorrido que devuelve elementos ordenados).

**Dónde se usa:**
| Uso | Razón |
|-----|-------|
| `Parque.catalogoAtracciones` | Búsqueda rápida de atracción por ID desde cualquier endpoint |
| `Parque.catalogoUsuarios` | Búsqueda de visitantes/operadores por número de documento |
| `Parque.catalogoZonas` | Búsqueda de zona por ID |
| `Grafo.catalogoNodos` | Localización O(log n) de nodos durante Dijkstra y BFS |

**Por qué BST y no HashMap:**  
Es un requisito académico. Adicionalmente, `inorden()` devuelve los elementos ordenados, lo que es útil para generar reportes y listar entidades en orden alfabético.

---

### 4. `Grafo<T>` — Grafo No Dirigido con Pesos

**Paquete:** `techpark.estructuras.grafo`

**Implementación:**  
Lista de adyacencia implementada con `ListaEnlazada` para las aristas de cada nodo (`NodoGrafo`) y un `ArbolBST` interno como catálogo de nodos para búsqueda. El grafo es **no dirigido**: al agregar una arista A→B se agrega automáticamente B→A.

**Componentes:**
- `NodoGrafo<T>`: Nodo con id, dato genérico y lista enlazada de aristas (`ListaEnlazada<Arista<T>>`)
- `Arista<T>`: Arista con referencia a nodo origen, destino y peso (distancia en metros)
- `EntradaDijkstra<T>`: Entrada comparable para la cola de prioridad de Dijkstra

**Dónde se usa:**
| Uso | Razón |
|-----|-------|
| `Parque.mapa` | Representa el mapa físico del parque; nodos = atracciones, aristas = senderos |

**Algoritmos implementados:**
- `dijkstra(origen, destino)` → ruta más corta entre dos atracciones por peso
- `bfs(inicio)` → recorrido en amplitud, útil para análisis de conectividad

---

### 5. `SetArbol<T>` — Conjunto sin Duplicados (basado en BST)

**Paquete:** `techpark.estructuras.conjunto`

**Implementación:**  
Wrapper del `ArbolBST<T>`. Garantiza unicidad: si la clave ya existe, `agregar()` devuelve `false` sin insertar. Expone `listar()` (inorden), `contiene()` y `buscar()`.

**Dónde se usa:**
| Uso | Razón |
|-----|-------|
| `Visitante.favoritos` | Un visitante no puede agregar la misma atracción dos veces a favoritos |
| `ServicioAlertas` (interno) | Evita notificar al mismo visitante dos veces cuando se cierra una atracción por clima |

---


### Flujo General del Sistema

```
Usuario (navegador)
      │
      ▼
Frontend Next.js (puerto 3000)
      │  HTTP REST (JSON)
      ▼
Spring Boot API (puerto 8080)
      │
      ▼
AppState (@Component singleton)
      │
      ├── Parque (raíz del dominio)
      │     ├── ArbolBST<Atraccion>   ← catalogoAtracciones
      │     ├── ArbolBST<Usuario>     ← catalogoUsuarios
      │     ├── ArbolBST<Zona>        ← catalogoZonas
      │     └── Grafo<Atraccion>      ← mapa físico
      │
      └── Servicios (sin base de datos; estado en memoria)
            ├── ServicioAcceso
            ├── ServicioAlertas  (ColaPrioridad<AlertaMantenimiento>)
            ├── ServicioColas
            ├── ServicioOperador
            ├── ServicioParque
            ├── ServicioAdministracion
            ├── ServicioReportes
            ├── ServicioShows
            └── ServicioAutenticacion
```

> ⚠️ **Nota arquitectónica:** El sistema no usa base de datos. Todo el estado vive en memoria en el bean `AppState`. Esto es intencional para el alcance académico y permite demostrar las estructuras de datos puras.

---

##  Tecnologías Utilizadas

### Backend
| Tecnología | Versión                        | Uso |
|------------|--------------------------------|-----|
| Java | 25                             | Lenguaje principal |
| Spring Boot | 3.3.5                          | Framework REST |
| Spring Boot Starter Web | 3.3.5                          | Controladores HTTP y serialización JSON |
| JUnit 5 | (via spring-boot-starter-test) | Pruebas unitarias |
| Maven | 3.x                            | Gestión de dependencias y build |

### Frontend
| Tecnología | Versión | Uso |
|------------|---------|-----|
| Next.js | 15.x | Framework React con App Router |
| TypeScript | 5.x | Tipado estático |
| Tailwind CSS | 3.x | Estilos utilitarios |
| shadcn/ui | latest | Componentes de interfaz |
| pnpm | 9.x | Gestión de paquetes |

### Herramientas
| Herramienta | Uso |
|------------|-----|
| Git / GitHub | Control de versiones y colaboración |
| IntelliJ IDEA / VS Code | IDEs utilizados |

---

## ️ Requisitos del Sistema

- **Java 25** o superior (LTS recomendado)
- **Maven 3.6+** (o usar el wrapper `mvnw` incluido)
- **Node.js 18+** con **pnpm** instalado globalmente
- Puerto **8080** disponible (backend)
- Puerto **3000** disponible (frontend)

```bash
# Verificar versiones
java --version      # debe ser 25
mvn --version       # debe ser 3.6+
node --version      # debe ser 18+
pnpm --version      # debe ser 9+
```

---

##  Instalación y Ejecución

### 1. Clonar el Repositorio

```bash
git clone https://github.com/dianatijeras/ProyectoFinalEstructuras.git
cd proyectoFinalEstructuras
```

### 2. Ejecutar el Backend (Spring Boot)

```bash
# Entrar al directorio del backend
cd backendTechPark

# Compilar el proyecto
mvn clean compile

# Ejecutar el servidor
mvn spring-boot:run
```

El backend queda disponible en `http://localhost:8080`.  
Al iniciar, `AppState` carga automáticamente el escenario de prueba con 8 atracciones, 3 zonas, operadores y visitantes.

**Credenciales de prueba precargadas:**
| Rol | Documento | Contraseña |
|-----|-----------|------------|
| Administrador | `admin` | `admin123` |
| Operador 1 | `op1` | `123` |
| Operador 2 | `op2` | `123` |
| Visitante 1 | `vis1` | `123` |
| Visitante Fast-Pass | `vis4` | `123` |

### 3. Ejecutar el Frontend (Next.js)

```bash
# En otra terminal, entrar al directorio del frontend
cd frontendTechPark

# Instalar dependencias
npm install

# Ejecutar en modo desarrollo
npm dev
```

El frontend queda disponible en `http://localhost:3000`.

La URL del backend se configura en `.env.local`:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080
```

### 4. Modo Consola (sin Spring Boot)

Para ejecutar el sistema desde la terminal sin levantar el servidor:

```bash
cd backendTechPark
mvn compile exec:java -Dexec.mainClass="techpark.MainConsola"
```

### 5. Ejecutar las Pruebas JUnit

```bash
cd backendTechPark
mvn test
```

---

##  Pruebas Unitarias

El proyecto cuenta con **dos suites de pruebas**:

### Suite JUnit 5 — `TechParkSistemaTest.java`

Ubicación: `src/test/java/techpark/TechParkSistemaTest.java`

| Prueba | Qué valida |
|--------|-----------|
| `debeCrearVisitanteConDatosValidos` | Construcción correcta de un visitante con sus atributos |
| `debeNormalizarSaldoNegativoDelVisitanteACero` | Saldo negativo se normaliza a 0 al construir el visitante |
| `debeRechazarEdadYEstaturaNegativasAlActualizarVisitante` | Validación de negativos en actualización de perfil |
| `debeComprarTicketGeneralCorrectamente` | Venta de ticket GENERAL con aforo y precio correctos |
| `debeComprarTicketFastPassYConservarTipoYPrioridad` | Ticket Fast-Pass tiene prioridad 1 |
| `colaVirtualDebeProcesarPrimeroVisitanteFastPass` | El heap extrae Fast-Pass antes que General |
| *(y más...)* | Cobertura de acceso, alertas climáticas, mantenimiento, BST, grafo |

```bash
# Ejecutar todas las pruebas JUnit
mvn test

# Ver reporte de resultados
cat target/surefire-reports/*.txt
```


---

## Roles del Sistema

###  Visitante
Accede mediante documento y contraseña. Puede:
- Comprar un ticket (GENERAL, FAMILIAR o FAST_PASS)
- Unirse a la cola virtual de una atracción
- Consultar su posición en la cola
- Ver el mapa del parque y calcular rutas óptimas
- Agregar y consultar atracciones favoritas
- Consultar su historial de visitas
- Recibir notificaciones de shows, clima e incidentes
- Recargar su saldo virtual

###  Operador
Accede con su documento. Solo puede gestionar las atracciones de **su zona asignada**:
- Llamar al siguiente visitante de la cola
- Cambiar el estado de una atracción (ACTIVA / EN_MANTENIMIENTO / CERRADA)
- Ver las alertas de mantenimiento de sus atracciones

### Administrador
Acceso total al sistema:
- Crear, modificar zonas y atracciones
- Crear y asignar operadores a zonas
- Crear y modificar perfiles de visitantes
- Activar y finalizar alertas climáticas
- Registrar y resolver incidentes operativos
- Generar reportes de jornada
- Cargar escenarios de datos desde CSV

---

##  Algoritmos Importantes

### Dijkstra — Ruta Más Corta

**Clase:** `Grafo.dijkstra(idOrigen, idDestino)`

Implementación propia que utiliza la `ColaPrioridad<EntradaDijkstra<T>>` del proyecto como la cola de nodos pendientes. El algoritmo usa un `ArbolBST<RegistroDijkstra<T>>` para almacenar distancias y predecesores en lugar de un HashMap, manteniendo la consistencia con las estructuras propias.

```
Resultado: Lista ordenada de NodoGrafo desde origen hasta destino
```

### BFS — Búsqueda en Amplitud

**Clase:** `Grafo.bfs(idInicio)`

Implementación con `ListaEnlazada` como cola implícita (acceso por índice con `obtener(posicion++)`) y `SetArbol` para marcar nodos visitados. Útil para verificar la conectividad del parque y generar el recorrido completo.

### Mantenimiento Preventivo

**Clase:** `ServicioAlertas.evaluarMantenimiento()`

Lógica disparada automáticamente cada vez que un visitante accede a una atracción (`Atraccion.incrementarVisitantes()`). Si `contadorAcumuladoVisitantes >= 500`, la atracción cambia a EN_MANTENIMIENTO y se inserta una `AlertaMantenimiento` en la cola de prioridad de alertas.

### Control de Acceso

**Clase:** `ServicioAcceso.validarYRegistrarAcceso()`

Cadena de validaciones en orden:
1. Ticket activo
2. Atracción en estado ACTIVA
3. Zona con aforo disponible
4. Capacidad del ciclo actual no superada
5. Edad mínima cumplida
6. Estatura mínima cumplida
7. Saldo virtual suficiente (solo si hay costo adicional y ticket GENERAL)

Si todas pasan → registra la visita en el historial, descuenta saldo, incrementa contadores.

### Prioridad en Cola

**Clase:** `EntradaEnCola.compareTo()`

```java
// Prioridad 1 = Fast-Pass, Prioridad 2 = General/Familiar
int cmp = Integer.compare(this.prioridad, otra.prioridad);
if (cmp != 0) return cmp;
// Empate → FIFO por hora de llegada
return this.horaIngreso.compareTo(otra.horaIngreso);
```

---

## API REST — Endpoints Principales

El servidor corre en `http://localhost:8080`. Todas las respuestas siguen el formato:
```json
{ "ok": true, "mensaje": "...", "data": { ... } }
```

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| POST | `/api/auth/login` | Iniciar sesión |
| GET | `/api/parque/resumen` | Estado general del parque |
| GET | `/api/atracciones` | Listar todas las atracciones |
| GET | `/api/atracciones/{id}` | Buscar atracción por ID (usa BST) |
| PATCH | `/api/atracciones/{id}/estado` | Cambiar estado (operador) |
| POST | `/api/atracciones/{id}/revision` | Registrar revisión técnica |
| GET | `/api/zonas` | Listar zonas |
| POST | `/api/colas/{idAtraccion}/unirse` | Unirse a cola virtual |
| GET | `/api/colas/{idAtraccion}` | Ver cola de una atracción |
| POST | `/api/colas/{idAtraccion}/siguiente` | Llamar siguiente (operador) |
| GET | `/api/mapa` | Obtener grafo del parque |
| GET | `/api/mapa/ruta` | Calcular ruta óptima (Dijkstra) |
| GET | `/api/mapa/bfs/{idInicio}` | Recorrido BFS |
| POST | `/api/alertas/climatica` | Activar alerta climática |
| DELETE | `/api/alertas/climatica/{id}` | Finalizar alerta climática |
| GET | `/api/alertas/mantenimiento` | Ver alertas de mantenimiento |
| GET | `/api/reportes/jornada` | Reporte de cierre de jornada |
| POST | `/api/datos/cargar` | Cargar escenario desde CSV |
| GET | `/api/visitantes/{doc}/historial` | Historial de visitas (lista enlazada) |
| GET | `/api/visitantes/{doc}/favoritos` | Favoritos (set BST) |
| POST | `/api/visitantes/{doc}/favoritos/{idAtr}` | Agregar favorito |
| GET | `/api/shows` | Listar shows del día |

---

##  Carga de Datos Iniciales

### Escenario automático
Al iniciar, `DatosIniciales.java` carga automáticamente:
- 3 zonas (Zona Aventura, Zona Familiar, Zona Acuática)
- 8 atracciones conectadas mediante senderos
- 2 operadores asignados a zonas
- 4 visitantes de prueba (uno de ellos con Fast-Pass precargado)
- 1 administrador
- Shows del café programados

### Carga desde CSV
Formato del archivo:

```
PARQUE,id,nombre,capacidadMaxima
ZONA,id,nombre,capacidadMaxima,disponible
ATRACCION,id,nombre,tipo,zonaId,capacidad,alturaMin,edadMin,costo,estado,tiempoEspera
VISITANTE,nombre,documento,edad,password,estatura,saldo
OPERADOR,nombre,documento,edad,password,zonaId
SENDERO,idOrigen,idDestino,peso
TICKET,documentoVisitante,tipo
SHOW,id,nombre,zonaId,horario,duracion
ALERTA_CLIMA,id,tipo,fecha,activa
```

El administrador puede cargar el archivo desde el panel de administración en el frontend.

---

##  Diagrama de Paquetes

```
techpark/
├── modelo/          → Entidades puras del dominio (sin Spring, sin frameworks)
├── estructuras/     → Implementaciones propias de ED (sin java.util salvo ArrayList/List)
├── servicios/       → Lógica de negocio (orquesta modelo + estructuras)
├── api/             → Adaptadores REST (controllers, DTOs, mappers)
├── config/          → Configuración Spring (AppState, CORS)
├── datos/           → Carga de escenarios (hardcoded + CSV)
├── enums/           → Tipos enumerados del dominio
├── controladores/   → Controladores MVC para modo consola
├── gui/             → Interfaz Swing (modo escritorio alternativo)
├── pruebas/         → Suite de pruebas propias
└── utilidades/      → Herramientas transversales (GeneradorId, Validador)
```

---

## 👨‍💻 Autores

| Nombre                        | GitHub                               |
|-------------------------------|--------------------------------------|
| *Juan Jose Suarez Moncaleano* | [@juans9432](https://github.com/juans9432)    |  |
| *Diana Maria Garcia Alzatte*  | [@dianatijeras](https://github.com/dianatijeras) |  |

---

**Universidad del Quindío — Ingeniería de Sistemas y Computacion**  
*Materia: Estructura de Datos*  
*Semestre: 2026-1*

> Este proyecto fue desarrollado con fines académicos. Las estructuras de datos implementadas son originales.