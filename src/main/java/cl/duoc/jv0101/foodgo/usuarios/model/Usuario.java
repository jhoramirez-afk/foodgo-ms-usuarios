package cl.duoc.jv0101.foodgo.usuarios.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
import java.math.BigDecimal;


@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nombre es obligatorio")
    @Size(max = 255, message = "El campo admite hasta 255 caracteres")
    @Column(nullable = false)
    private String nombre;
    @NotBlank(message = "Rol es obligatorio")
    @Pattern(regexp = "CLIENTE|RESTAURANTE|REPARTIDOR", message = "Rol debe ser CLIENTE, RESTAURANTE, REPARTIDOR")
    @Column(nullable = false)
    private String rol;
    @NotBlank(message = "Correo es obligatorio")
    @Email(message = "El correo debe tener un formato válido")
    @Size(max = 254, message = "El campo admite hasta 254 caracteres")
    @Column(nullable = false, unique = true, length = 254)
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
