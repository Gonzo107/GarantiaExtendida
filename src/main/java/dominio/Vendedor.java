package dominio;

import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;
import dominio.utils.ProveedorTiempo;
import lombok.RequiredArgsConstructor;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RequiredArgsConstructor
public class Vendedor {

    public static final String EL_PRODUCTO_TIENE_GARANTIA = "El producto ya cuenta con una garantia extendida";
    public static final String EL_PRODUCTO_NO_CUENTA_CON_GARANTIA = "Este producto no cuenta con garant�a extendida";

    public static final String PATRON_SOLO_INCLUYE_TRES_VOCALES = "^[^aeiou]*[aeiou][^aeiou]*[aeiou][^aeiou]*[aeiou][^aeiou]*$";
    public static final int UMBRAL_SOBRECOSTO_GARANTIA = 500000;
    public static final double PORCENTAJE_COSTO_GARANTIA_SOBRE_UMBRAL = 0.2;
    public static final double PORCENTAJE_COSTO_GARANTIA_ESTANDAR = 0.1;
    public static final int DIAS_VIGENCIA_GARANTIA_SOBRE_UMBRAL = 200;
    public static final int DIAS_VIGENCIA_GARANTIA_ESTANDAR = 100;

    private final RepositorioProducto repositorioProducto;
    private final RepositorioGarantiaExtendida repositorioGarantia;
    private final ProveedorTiempo proveedorTiempo;


    public void generarGarantia(String codigo, String nombreCliente) {

        validarCoberturaGarantia(codigo);
        validarExistenciaGarantia(codigo);

        Producto producto = repositorioProducto.obtenerPorCodigo(codigo);

        double precioGarantia = calcularCostoGarantia(producto.getPrecio());

        Date fechaSolicitudGarantia = proveedorTiempo.obtenerFechaActual();

        Date fechaFinalizacionGarantia = calcularFechaFinalizacioGarantia(producto.getPrecio(), fechaSolicitudGarantia);

        GarantiaExtendida garantiaExtendida = new GarantiaExtendida(producto, fechaSolicitudGarantia,
                fechaFinalizacionGarantia, precioGarantia, nombreCliente);

        repositorioGarantia.agregar(garantiaExtendida);
    }

    public boolean tieneGarantia(String codigo) {
        Producto producto = repositorioGarantia.obtenerProductoConGarantiaPorCodigo(codigo);

        return producto != null;
    }

    private void validarCoberturaGarantia(String codigo) {
        Pattern pattern = Pattern
                .compile(PATRON_SOLO_INCLUYE_TRES_VOCALES, Pattern.MULTILINE | Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(codigo);

        if (matcher.matches()) {
            throw new GarantiaExtendidaException(EL_PRODUCTO_NO_CUENTA_CON_GARANTIA);
        }
    }

    private void validarExistenciaGarantia(String codigo) {
        if (tieneGarantia(codigo)) {
            throw new GarantiaExtendidaException(EL_PRODUCTO_TIENE_GARANTIA);
        }
    }

    private double calcularCostoGarantia(double precio) {
        if (precio > UMBRAL_SOBRECOSTO_GARANTIA) {
            return precio * PORCENTAJE_COSTO_GARANTIA_SOBRE_UMBRAL;
        }

        return precio * PORCENTAJE_COSTO_GARANTIA_ESTANDAR;
    }

    private Date calcularFechaFinalizacioGarantia(double precio, Date fechaInicio) {
        LocalDate localDate = fechaInicio.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        boolean sobrepasaUmbral = precio > UMBRAL_SOBRECOSTO_GARANTIA;

        int diasASumar = sobrepasaUmbral ? DIAS_VIGENCIA_GARANTIA_SOBRE_UMBRAL : DIAS_VIGENCIA_GARANTIA_ESTANDAR;

        int diasSumados = 0;

        while (diasSumados < diasASumar) {
            if (sobrepasaUmbral && localDate.getDayOfWeek() != DayOfWeek.MONDAY) {
                diasSumados++;
            }
            if (!sobrepasaUmbral) {
                diasSumados++;
            }
            localDate = localDate.plusDays(1);
        }

        if (sobrepasaUmbral && localDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            localDate = localDate.plusDays(1);
        }

        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

    }

}
