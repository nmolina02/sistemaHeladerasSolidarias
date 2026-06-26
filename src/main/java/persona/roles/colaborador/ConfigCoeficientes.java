package persona.roles.colaborador;

import colaboraciones.TipoColaboracion;
import lombok.Getter;
import java.util.HashMap;

@Getter
public class ConfigCoeficientes {
    private HashMap<TipoColaboracion, Float> coefPorTipoColaboracion = new HashMap<TipoColaboracion, Float>();
    private static ConfigCoeficientes instance;

    private ConfigCoeficientes(){
        coefPorTipoColaboracion.put(TipoColaboracion.DONACION_DE_DINERO, 0.5f);
        coefPorTipoColaboracion.put(TipoColaboracion.DISTRIBUCION_DE_VIANDAS, 1f);
        coefPorTipoColaboracion.put(TipoColaboracion.DONACION_DE_VIANDAS, 1.5f);
        coefPorTipoColaboracion.put(TipoColaboracion.REGISTRO_DE_PERSONAS_VULNERABLES, 2f);
        coefPorTipoColaboracion.put(TipoColaboracion.HACERSE_CARGO, 5f);
        coefPorTipoColaboracion.put(TipoColaboracion.OFRECER_PRODUCTO_RECONOCIMIENTO, 0f);
    }

    public static ConfigCoeficientes getInstance() {
        if (instance == null) {
            instance = new ConfigCoeficientes();
        }
        return instance;
    }

    public float getCoeficiente(TipoColaboracion tipoColaboracion) {
        return coefPorTipoColaboracion.get(tipoColaboracion);
    }
}
