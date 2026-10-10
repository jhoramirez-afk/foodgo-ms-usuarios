package cl.duoc.jv0101.foodgo.usuarios.service;

import java.util.List;
import java.util.Locale;
import cl.duoc.jv0101.foodgo.usuarios.exception.ResourceConflictException;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.usuarios.model.Usuario;
import cl.duoc.jv0101.foodgo.usuarios.repository.UsuarioRepository;

@Service
@Transactional
public class UsuarioService {

    private final UsuarioRepository repository;

    public UsuarioService(UsuarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<Usuario> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Usuario> findById(Long id) {
        return repository.findById(id);
    }

    public Usuario create(Usuario recurso) {
        recurso.setId(null);
        recurso.getDirecciones().forEach(item -> item.setId(null));
        recurso.setEmail(recurso.getEmail().toLowerCase(Locale.ROOT));
        if (repository.existsByEmailIgnoreCase(recurso.getEmail())) {
            throw new ResourceConflictException("Ya existe un usuario con ese correo");
        }
        return repository.save(recurso);
    }

    public Optional<Usuario> update(Long id, Usuario datos) {
        return repository.findById(id).map(existente -> {
            String email = datos.getEmail().toLowerCase(Locale.ROOT);
            if (repository.existsByEmailIgnoreCaseAndIdNot(email, id)) {
                throw new ResourceConflictException("Ya existe un usuario con ese correo");
            }
            existente.setNombre(datos.getNombre());
            existente.setRol(datos.getRol());
            existente.setEmail(email);
            return repository.save(existente);
        });
    }

    public boolean delete(Long id) {
        return repository.findById(id).map(existente -> {
            repository.delete(existente);
            return true;
        }).orElse(false);
    }
}
