package tiempo.fecha;

import dominio.utils.ProveedorTiempo;

import java.util.Date;

public class ProveedorFecha implements ProveedorTiempo {
    public Date obtenerFechaActual() {
        return new Date();
    }


}
