package dominio;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Producto {

    private final String codigo;
    private final String nombre;
    private final double precio;

}
