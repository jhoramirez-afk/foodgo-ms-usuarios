package cl.duoc.jv0101.foodgo.usuarios.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.CascadeType;
import jakarta.persistence.OneToMany;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import java.math.BigDecimal;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Column(nullable = false)
    private String nombre;
    @Column
    private String rol;
    @Column
    private String email;

    @Valid
    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("usuario-direcciones")
    private List<Direccion> direcciones = new ArrayList<>();

    public Long getId() { return id; }

    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }

    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }

    public void setRol(String rol) { this.rol = rol; }

    public String getEmail() { return email; }

    public void setEmail(String email) { this.email = email; }

    public List<Direccion> getDirecciones() {
        return direcciones;
    }

    public void setDirecciones(List<Direccion> items) {
        this.direcciones.clear();
        if (items != null) {
            items.forEach(this::addDireccion);
        }
    }

    public void addDireccion(Direccion item) {
        direcciones.add(item);
        item.setUsuario(this);
    }

    public void removeDireccion(Direccion item) {
        direcciones.remove(item);
        item.setUsuario(null);
    }
}
