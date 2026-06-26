package receptorDeJSON;

import lombok.Getter;
import org.json.JSONObject;
import repository.RepositoryUsuarioSSO;

@Getter
public class UsuariosRecibidosSSO {
    private JSONObject userInfo;
    private String id;

    public UsuariosRecibidosSSO(JSONObject userInfo, String id) {
        this.userInfo = userInfo;
        this.id = id;
        RepositoryUsuarioSSO.getInstance().addUsuarioSSO(this);
    }
}