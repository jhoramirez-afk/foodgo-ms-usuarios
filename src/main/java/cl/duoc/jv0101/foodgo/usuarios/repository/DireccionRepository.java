package cl.duoc.jv0101.foodgo.usuarios.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import cl.duoc.jv0101.foodgo.usuarios.model.Direccion;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuario_Id(Long usuarioId);
}
