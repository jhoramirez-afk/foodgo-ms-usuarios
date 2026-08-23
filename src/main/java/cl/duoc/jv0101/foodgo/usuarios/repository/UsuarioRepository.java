package cl.duoc.jv0101.foodgo.usuarios.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.usuarios.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
}
