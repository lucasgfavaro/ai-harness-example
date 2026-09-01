# Home Agent Harness

Modulo Spring Boot que ejecuta el agent loop. Usa OpenAI solamente con `dryRun=false` y delega las acciones de la luz a `home-assistant-api` mediante HTTP.

Este modulo forma parte del monorepo. Consulta [`../README.md`](../README.md) para levantar ambos servicios con Docker Compose, ejecutar las pruebas y configurar variables.

Desde la raiz:

```powershell
.\gradlew.bat :home-agent-harness:bootRun
```
