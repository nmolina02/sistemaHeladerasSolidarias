package persona.roles.colaborador.OpcionesSuscripciones;

import lombok.Getter;

@Getter
public class OpcionSuscripcion {
    private OpcionesSuscripcion opcion;
    private int valor;

    public OpcionSuscripcion(OpcionesSuscripcion opcion, int valor) {
        this.opcion = opcion;
        this.valor = valor;
    }
}
