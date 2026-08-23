# Usuario — Resumen del microservicio

## Propósito

Administra la identidad, autenticación y perfiles de los tres actores de foodgo (clientes, restaurantes, repartidores).

## Contexto

- **Caso**: FoodGo (delivery de comida a domicilio)
- **Microservicio**: usuarios
- **Base path**: `/api/usuarios`

## Responsabilidad única (SRP)

Identidad y autenticación. El servicio atiende un único dominio de negocio y tiene una sola razón de cambio. Entrega su propia base de datos en memoria (H2) y expone su API REST de forma independiente, garantizando **bajo acoplamiento** y **alta cohesión** dentro de la arquitectura de microservicios del caso.

## Requisitos del caso que cubre

RF-01 (registro y autenticación), RNF-04 (seguridad: JWT por rol, aislado por credenciales)

## Stack tecnológico

| Componente | Tecnología |
|---|---|
| Framework | Spring Boot 3.3 |
| Lenguaje | Java 21 |
| Build | Maven |
| Persistencia | Spring Data JPA + H2 (in-memory) |
| Validación | Bean Validation (`jakarta.validation`) |
| API/Docs | springdoc-openapi — Swagger UI + OpenAPI yaml + ReDoc |
| Calidad | JaCoCo (cobertura LINE 100%) + Cucumber (BDD REST) |
| Contenedores | Docker + Docker Compose |

## Entradas disponibles desde la web (`/`)

La página raíz presenta el servicio y enlaza Swagger UI, OpenAPI yaml, ReDoc y la consola H2.
