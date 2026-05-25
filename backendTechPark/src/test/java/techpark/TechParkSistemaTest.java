package techpark;

import org.junit.jupiter.api.Test;
import techpark.model.usuarios.Visitante;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class TechParkSistemaTest {

    @Test
    void debeCrearVisitanteConDatosValidos() {
        Visitante visitante = new Visitante("VIS-T01", "Juan Perez", "9001", 20, "123", 1.75, 50000, null);

        assertEquals("Juan Perez", visitante.getNombre());
        assertEquals("9001", visitante.getDocumento());
        assertEquals(20, visitante.getEdad());
        assertEquals(1.75, visitante.getEstatura());
        assertEquals(50000, visitante.getSaldoVirtual());
    }

    @Test
    void debeNormalizarSaldoNegativoDelVisitanteACero() {
        Visitante visitante = new Visitante("VIS-T02", "Ana Gomez", "9002", 18, "123", 1.60, -20000, null);

        assertEquals(0, visitante.getSaldoVirtual());
    }

    @Test
    void debeRechazarEdadYEstaturaNegativasAlActualizarVisitante() {
        Visitante visitante = new Visitante("VIS-T03", "Carlos Ruiz", "9003", 22, "123", 1.80, 40000, null);

        assertThrows(IllegalArgumentException.class,
                () -> visitante.actualizarPerfil("Carlos Ruiz", -1, 1.80, 40000));

        assertThrows(IllegalArgumentException.class,
                () -> visitante.actualizarPerfil("Carlos Ruiz", 22, -1.50, 40000));
    }
}
