package persistencia.entitad;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity(name = "Producto")
@NamedQuery(name = "Producto.findByCodigo", query = "SELECT producto FROM Producto producto WHERE producto.codigo = :codigo")
public class ProductoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private double precio;

}
