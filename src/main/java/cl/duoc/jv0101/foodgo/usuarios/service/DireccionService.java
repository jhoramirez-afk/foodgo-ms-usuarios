package cl.duoc.jv0101.foodgo.usuarios.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cl.duoc.jv0101.foodgo.usuarios.exception.ResourceNotFoundException;
import cl.duoc.jv0101.foodgo.usuarios.model.Direccion;
import cl.duoc.jv0101.foodgo.usuarios.model.Usuario;
import cl.duoc.jv0101.foodgo.usuarios.repository.DireccionRepository;
import cl.duoc.jv0101.foodgo.usuarios.repository.UsuarioRepository;

@Service
@Transactional
public class DireccionService {

    private final DireccionRepository repository;
    private final UsuarioRepository usuarioRepository;

    public DireccionService(DireccionRepository repository, UsuarioRepository usuarioRepository) {
        this.repository = repository;
        this.usuarioRepository = usuarioRepository;
    }

    @Transactional(readOnly = true)
    public List<Direccion> findByUsuarioId(Long usuarioId) {
        if (!usuarioRepository.existsById(usuarioId)) {
            throw new ResourceNotFoundException("Usuario no encontrado con id " + usuarioId);
        }
        return repository.findByUsuario_Id(usuarioId);
    }

    @Transactional(readOnly = true)
    public Direccion findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrado con id " + id));
    }

    public Direccion create(Long usuarioId, Direccion recurso) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id " + usuarioId));
        recurso.setId(null);
        recurso.setUsuario(usuario);
        return repository.save(recurso);
    }

    public Direccion update(Long id, Direccion datos) {
        Direccion existente = findById(id);
        existente.setAlias(datos.getAlias());
        existente.setCalle(datos.getCalle());
        existente.setComuna(datos.getComuna());
        return repository.save(existente);
    }

    public void delete(Long id) {
        Direccion existente = findById(id);
        repository.delete(existente);
    }
}
