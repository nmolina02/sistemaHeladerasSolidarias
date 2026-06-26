/*package org.example;

import validador.condicion.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import validador.verificador.Verificador;


class VerificadorTest {
    private Verificador verificador;

    @BeforeEach
    public void init() {
        Longitud condicionLongitud = new Longitud();
        Credencial condicionCredencia = new Credencial();
        Secuencia condicionSecuencia = new Secuencia();
        Repeticiones condicionRepeticion = new Repeticiones();
        Rockyou condicionRockyou = new Rockyou();
        verificador = Verificador.getInstance();
        verificador.agregarCondiciones(condicionLongitud, condicionCredencia, condicionSecuencia, condicionRepeticion, condicionRockyou);
    }

    @Test
    public void contraseniaValida(){
        Assertions.assertEquals(
                true, verificador.validarContrasenia("Pepito", "etcetera123"));
    }

    @Test
    public void contraseniaCorta(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan Carlos", "corta");
        });

        String expectedMessage = "La contraseña debe tener entre 8 y 64 caracteres.Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

            Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaSimilarAlNombre(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "Juan1234");
        });

        String expectedMessage = "La contraseña es similar a su nombre de usuario. Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

            Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaEnElRockyou_p1(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "iloveyou");
        });

        String expectedMessage = "Su contraseña no es segura. Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

            Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaEnElRockyou_p2(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "spoed1992");
        });

        String expectedMessage = "Su contraseña no es segura. Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaEnElRockyou_p3(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "janeida28");
        });

        String expectedMessage = "Su contraseña no es segura. Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaEnElRockyou_p4(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "PAPASQUIANO");
        });

        String expectedMessage = "Su contraseña no es segura. Por favor, intente con otra contraseña";
        String actualMessage = exception.getMessage();

        Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaRepetitiva(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Juan", "jjjuancito");
        });

        String expectedMessage = "La contraseña contiene caracteres repetidos. Por favor, intente con una contraseña diferente.";
        String actualMessage = exception.getMessage();

            Assertions.assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    public void contraseniaConSecuencia(){
        Exception exception = Assertions.assertThrows(RuntimeException.class, () -> {
            verificador.validarContrasenia("Denise", "12345678");
        });

        String expectedMessage = "La contraseña contiene una secuencia. Por favor, intente con una contraseña diferente.";
        String actualMessage = exception.getMessage();

            Assertions.assertTrue(actualMessage.contains(expectedMessage));

    }

}
*/