package dominio.unitaria;

import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.Vendedor;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;
import tiempo.fecha.ProveedorFecha;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import testdatabuilder.ProductoTestDataBuilder;

import java.util.Date;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class VendedorTest {

    private static final String CODIGO_SIN_COBERTURA = "F01TSAEI50";
    private static final String NOMBRE_CLIENTE = "TEST";

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    RepositorioGarantiaExtendida repositorioGarantiaExtendida;

    @Mock
    RepositorioProducto repositorioProducto;

    @Mock
    ProveedorFecha proveedorFecha;

    @InjectMocks
    Vendedor vendedor;


    @Test
    public void productoYaTieneGarantiaTest() {

        // arrange
        ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

        Producto producto = productoTestDataBuilder.build();

        when(repositorioGarantiaExtendida.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()))
                .thenReturn(producto);

        when(proveedorFecha.obtenerFechaActual()).thenReturn(new Date());

        // act
        boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

        //assert
        assertTrue(existeProducto);
    }

    @Test
    public void productoNoTieneGarantiaTest() {

        // arrange
        ProductoTestDataBuilder productoestDataBuilder = new ProductoTestDataBuilder();

        Producto producto = productoestDataBuilder.build();

        when(repositorioGarantiaExtendida.obtenerProductoConGarantiaPorCodigo(producto.getCodigo())).thenReturn(null);

        when(proveedorFecha.obtenerFechaActual()).thenReturn(new Date());

        // act
        boolean existeProducto = vendedor.tieneGarantia(producto.getCodigo());

        //assert
        assertFalse(existeProducto);
    }

    @Test
    public void generarGarantiaSobreProductoTest() throws GarantiaExtendidaException {

        // arrange
        ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

        Producto producto = productoTestDataBuilder.build();

        when(repositorioGarantiaExtendida.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()))
                .thenReturn(null);

        when(proveedorFecha.obtenerFechaActual()).thenReturn(new Date());

        when(repositorioProducto.obtenerPorCodigo(producto.getCodigo())).thenReturn(producto);

        doNothing().when(repositorioGarantiaExtendida).agregar(any(GarantiaExtendida.class));

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

    }

    @Test
    public void generarGarantiaSobreProductoSinCoberturaFallaTest() throws GarantiaExtendidaException {

        // arrange
        ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder().conCodigo(CODIGO_SIN_COBERTURA);

        Producto producto = productoTestDataBuilder.build();

        //assert
        thrown.expect(GarantiaExtendidaException.class);
        thrown.expectMessage(is("Este producto no cuenta con garant�a extendida"));


        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

    }

    @Test
    public void generarGarantiaSobreProductoConGarantiaFallaTest() throws GarantiaExtendidaException {

        // arrange
        ProductoTestDataBuilder productoTestDataBuilder = new ProductoTestDataBuilder();

        Producto producto = productoTestDataBuilder.build();

        when(repositorioGarantiaExtendida.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()))
                .thenReturn(producto);

        //assert
        thrown.expect(GarantiaExtendidaException.class);
        thrown.expectMessage(is("El producto ya cuenta con una garantia extendida"));


        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

    }
}
