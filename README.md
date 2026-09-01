# AI Harness Example

Monorepo didactico con dos aplicaciones Spring Boot:

- `home-assistant-api`: simula una casa en memoria y publica APIs REST en el puerto `8080`.
- `home-agent-harness`: ejecuta un agent loop y usa la API de la luz como tool HTTP en el puerto `8081`.

```text
POST :8081/agent
       |
       v
home-agent-harness ---- OpenAI Responses API (dryRun=false)
       |
       +---- http://home-assistant-api:8080/api/light
```

Los dos modulos pertenecen a un unico repositorio Git y a un unico build Gradle multiproyecto.

## Levantar todo con Docker Compose

Requisitos: Docker Desktop iniciado y Docker Compose v2.

Para usar solamente `dryRun=true` no hace falta una clave. Para el flujo real, crea el archivo local de variables y completa `OPENAI_API_KEY`:

```powershell
Copy-Item .env.example .env
notepad .env
```

Construye y levanta ambos servicios:

```powershell
docker compose up --build
```

Compose espera a que la API acepte conexiones y conecta el harness mediante el hostname interno `home-assistant-api`; no hay que cambiar URLs manualmente.

Comprobaciones rapidas desde otra terminal:

```powershell
# Estado de la luz simulada
Invoke-RestMethod http://localhost:8080/api/light

# Agent loop local, sin OpenAI ni llamadas HTTP
$body = @{ request = 'encende la luz del jardin'; dryRun = $true } | ConvertTo-Json
Invoke-RestMethod -Method Post -ContentType 'application/json' -Body $body http://localhost:8081/agent
```

Con una clave configurada, cambia `dryRun` a `false` para que el modelo decida y el harness invoque la API del otro contenedor.

Swagger UI del harness: `http://localhost:8081/swagger-ui.html`.

Para detener y retirar los contenedores:

```powershell
docker compose down
```

## Ejecutar y probar sin Docker

Necesitas Java 21. El wrapper de Gradle esta en la raiz; no hace falta instalar Gradle.

Todas las pruebas:

```powershell
.\gradlew.bat clean test
```

Para desarrollo local, abre dos terminales desde la raiz:

```powershell
# Terminal 1
.\gradlew.bat :home-assistant-api:bootRun

# Terminal 2
$env:OPENAI_API_KEY = 'tu-clave-de-Platform' # solo para dryRun=false
.\gradlew.bat :home-agent-harness:bootRun
```

El valor local por defecto del harness es `http://localhost:8080/api/light`. En Compose se reemplaza con `HOME_LIGHT_BASE_URL=http://home-assistant-api:8080/api/light`.

## Estructura

```text
ai-harness-example/
|-- compose.yaml
|-- gradlew.bat
|-- settings.gradle
|-- home-assistant-api/
|   |-- Dockerfile
|   |-- build.gradle
|   `-- src/
`-- home-agent-harness/
    |-- Dockerfile
    |-- build.gradle
    `-- src/
```

Cada Dockerfile usa un build de dos etapas: compila el modulo con Java 21 y copia solamente el JAR ejecutable a una imagen JRE.

## Configuracion

| Variable | Uso | Valor por defecto |
|---|---|---|
| `OPENAI_API_KEY` | Flujo real del harness | vacio |
| `OPENAI_MODEL` | Modelo de OpenAI | `gpt-4.1-mini` |
| `HOME_API_PORT` | Puerto publicado de la API | `8080` |
| `HARNESS_PORT` | Puerto publicado del harness | `8081` |
| `HOME_LIGHT_BASE_URL` | URL interna usada por la tool | configurada por Compose |

No guardes `.env` ni claves en Git. `.env.example` contiene solamente valores de ejemplo.
