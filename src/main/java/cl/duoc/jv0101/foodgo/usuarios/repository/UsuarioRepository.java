package cl.duoc.jv0101.foodgo.usuarios.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.usuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    @Override
    @EntityGraph(attributePaths = "direcciones")
    List<Usuario> findAll();

    @Override
    @EntityGraph(attributePaths = "direcciones")
    Optional<Usuario> findById(Long id);
    boolean existsByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCaseAndIdNot(String email, Long id);
}
