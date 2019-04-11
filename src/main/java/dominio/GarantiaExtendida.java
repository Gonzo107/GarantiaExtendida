package dominio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Date;

@Getter
@RequiredArgsConstructor
public class GarantiaExtendida {

    private final Producto producto;
    private final Date fechaSolicitudGarantia;
    private final Date fechaFinGarantia;
    private final double precioGarantia;
    private final String nombreCliente;


}
