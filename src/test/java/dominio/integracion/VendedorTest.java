package dominio.integracion;

import dominio.GarantiaExtendida;
import dominio.Producto;
import dominio.Vendedor;
import dominio.excepcion.GarantiaExtendidaException;
import dominio.repositorio.RepositorioGarantiaExtendida;
import dominio.repositorio.RepositorioProducto;
import tiempo.fecha.ProveedorFecha;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import persistencia.sistema.SistemaDePersistencia;
import testdatabuilder.ProductoTestDataBuilder;

import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class VendedorTest {

    private static final String COMPUTADOR_LENOVO = "Computador Lenovo";
    private static final String CODIGO_SIN_COBERTURA = "F01TSAEI50";
    private static final String NOMBRE_CLIENTE = "TEST";
    private static final double PRECIO_BAJO_UMBRAL_GARANTIA =499999;

    private SistemaDePersistencia sistemaPersistencia;

    private RepositorioProducto repositorioProducto;
    private RepositorioGarantiaExtendida repositorioGarantia;
    private ProveedorFecha proveedorFecha;

    @Before
    public void setUp() {

        sistemaPersistencia = new SistemaDePersistencia();

        repositorioProducto = sistemaPersistencia.obtenerRepositorioProductos();
        repositorioGarantia = sistemaPersistencia.obtenerRepositorioGarantia();
        proveedorFecha = new ProveedorFecha();

        sistemaPersistencia.iniciar();
    }


    @After
    public void tearDown() {
        sistemaPersistencia.terminar();
    }

    @Test
    public void generarGarantiaTest() {

        // arrange
        Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();

        repositorioProducto.agregar(producto);

        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFecha);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        // assert
        Assert.assertTrue(vendedor.tieneGarantia(producto.getCodigo()));
        Assert.assertNotNull(repositorioGarantia.obtenerProductoConGarantiaPorCodigo(producto.getCodigo()));

    }

    @Test
    public void productoYaTieneGarantiaTest() {

        // arrange
        Producto producto = new ProductoTestDataBuilder().conNombre(COMPUTADOR_LENOVO).build();

        repositorioProducto.agregar(producto);

        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFecha);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        try {

            vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
            fail();

        } catch (GarantiaExtendidaException e) {
            // assert
            Assert.assertEquals(Vendedor.EL_PRODUCTO_TIENE_GARANTIA, e.getMessage());
        }
    }

    @Test
    public void productoNoCubiertoPorGarantiaDeberiaFallarTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().conCodigo(CODIGO_SIN_COBERTURA).build();

        repositorioProducto.agregar(producto);


        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFecha);

        // act
        try {
            vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);
            fail();
        } catch (GarantiaExtendidaException excepcion) {
            // assert
            Assert.assertEquals(Vendedor.EL_PRODUCTO_NO_CUENTA_CON_GARANTIA, excepcion.getMessage());
        }

    }

    @Test
    public void precioGarantiaDeberiaCalcularseCorrectamentePorEncimaDeUmbralTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().build();

        repositorioProducto.agregar(producto);


        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFecha);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());

        assertThat(garantiaExtendida.getPrecioGarantia(), is(producto.getPrecio() * 0.2));


    }

    @Test
    public void precioGarantiaDeberiaCalcularseCorrectamentePorDebajoDeUmbralTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_BAJO_UMBRAL_GARANTIA).build();

        repositorioProducto.agregar(producto);


        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFecha);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());

        assertThat(garantiaExtendida.getPrecioGarantia(), is(producto.getPrecio() * 0.1));


    }

    @Test
    public void fechaFinalizacionDeberiaCalcularseCorrectamentePorEncimaDeUmbralTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().build();

        repositorioProducto.agregar(producto);

        ProveedorFecha proveedorFechaMock = mock(ProveedorFecha.class);

        when(proveedorFechaMock.obtenerFechaActual()).thenReturn(new Date("08/16/2018"));

        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFechaMock);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());

        assertThat(garantiaExtendida.getFechaFinGarantia(), is(new Date("04/06/2019")));


    }

    @Test
    public void fechaFinalizacionDeberiaAumentarUnDiaEnDomingoTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().build();

        repositorioProducto.agregar(producto);

        ProveedorFecha proveedorFechaMock = mock(ProveedorFecha.class);

        when(proveedorFechaMock.obtenerFechaActual()).thenReturn(new Date("08/17/2018"));

        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFechaMock);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());

        assertThat(garantiaExtendida.getFechaFinGarantia(), is(new Date("04/08/2019")));


    }

    @Test
    public void fechaFinalizacionDeberiaCalcularseCorrectamentePorDebajoDeUmbralTest() {
        // arrange
        Producto producto = new ProductoTestDataBuilder().conPrecio(PRECIO_BAJO_UMBRAL_GARANTIA).build();

        repositorioProducto.agregar(producto);

        ProveedorFecha proveedorFechaMock = mock(ProveedorFecha.class);

        when(proveedorFechaMock.obtenerFechaActual()).thenReturn(new Date("08/16/2018"));

        Vendedor vendedor = new Vendedor(repositorioProducto, repositorioGarantia, proveedorFechaMock);

        // act
        vendedor.generarGarantia(producto.getCodigo(), NOMBRE_CLIENTE);

        GarantiaExtendida garantiaExtendida = repositorioGarantia.obtener(producto.getCodigo());

        assertThat(garantiaExtendida.getFechaFinGarantia(), is(new Date("11/24/2018")));


    }

}
