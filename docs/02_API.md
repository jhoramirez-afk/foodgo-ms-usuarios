# API REST: usuarios

Base local: http://localhost:8081/api. Swagger UI: http://localhost:8081/swagger-ui/index.html.

| Método | Ruta | HTTP de éxito |
|---|---|---:|
| POST | /usuarios | 201 |
| GET | /usuarios | 200 |
| GET | /usuarios/{id} | 200 |
| PUT | /usuarios/{id} | 200 |
| DELETE | /usuarios/{id} | 204 |
| POST | /usuarios/{id}/direcciones | 201 |
| GET | /usuarios/{id}/direcciones | 200 |
| GET | /direcciones/{id} | 200 |
| PUT | /direcciones/{id} | 200 |
| DELETE | /direcciones/{id} | 204 |

## Crear entidad principal

```json
{
  "nombre": "Camila Soto",
  "rol": "CLIENTE",
  "email": "camila.soto.DEMO@example.com"
}
```

## Crear entidad relacionada

```json
{
  "alias": "Casa",
  "calle": "Los Aromos 1450, departamento 302",
  "comuna": "Ñuñoa"
}
```

Usar el ID retornado por la creación del padre. Los ID son generados por la BD. Editar los hijos mediante sus propias rutas. Ver las reglas y los campos calculados en REGLAS_EP02.md.

Errores: 400 para datos o JSON inválidos; 404 para recurso/relación local inexistente; 409 para conflictos de integridad o unicidad cuando corresponda. Un campo demasiado largo devuelve 400. Los mensajes y validationErrors se entregan mediante ApiExceptionHandler.
