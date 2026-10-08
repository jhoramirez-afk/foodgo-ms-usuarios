package cl.duoc.jv0101.foodgo.usuarios.controller;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import cl.duoc.jv0101.foodgo.usuarios.model.Direccion;
import cl.duoc.jv0101.foodgo.usuarios.service.DireccionService;

@RestController
@RequestMapping("/api")
public class DireccionController {

    private final DireccionService service;

    public DireccionController(DireccionService service) {
        this.service = service;
    }

    @GetMapping("/usuarios/{usuarioId}/direcciones")
    public ResponseEntity<List<Direccion>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(service.findByUsuarioId(usuarioId));
    }

    @PostMapping("/usuarios/{usuarioId}/direcciones")
    public ResponseEntity<Direccion> crear(@PathVariable Long usuarioId, @Valid @RequestBody Direccion recurso) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(usuarioId, recurso));
    }

    @GetMapping("/direcciones/{id}")
    public ResponseEntity<Direccion> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PutMapping("/direcciones/{id}")
    public ResponseEntity<Direccion> actualizar(@PathVariable Long id, @Valid @RequestBody Direccion datos) {
        return ResponseEntity.ok(service.update(id, datos));
    }

    @DeleteMapping("/direcciones/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
