package repository;

import lombok.Getter;
import lombok.Setter;
import receptorDeJSON.UsuariosRecibidosSSO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryUsuarioSSO {
    private static RepositoryUsuarioSSO instancia = null;
    @Setter private List<UsuariosRecibidosSSO> usuariosSSO;

    private RepositoryUsuarioSSO() {
        this.usuariosSSO = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryUsuarioSSO getInstance() {
        if(instancia == null){
            instancia = new RepositoryUsuarioSSO();
        }
        return instancia;
    }

    public synchronized void addUsuarioSSO(UsuariosRecibidosSSO usuario) {
        usuariosSSO.add(usuario);
    }

    public UsuariosRecibidosSSO buscarUserInfo(String id) {
        return RepositoryUsuarioSSO.getInstance().getUsuariosSSO().stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}