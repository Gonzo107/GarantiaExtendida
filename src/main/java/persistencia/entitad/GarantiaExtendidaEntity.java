package persistencia.entitad;

import lombok.Data;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;

@Data
@Entity(name = "GarantiaExtendida")
@NamedQuery(name = "GarantiaExtendida.findByCodigo", query = "SELECT garantia from GarantiaExtendida garantia where garantia.producto.codigo = :codigo")
public class GarantiaExtendidaEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "ID_PRODUCTO", referencedColumnName = "id")
	private ProductoEntity producto;

	private Date fechaSolicitudGarantia;

	private Date fechaFinGarantia;

	private String nombreCliente;

	private double precio;


}
