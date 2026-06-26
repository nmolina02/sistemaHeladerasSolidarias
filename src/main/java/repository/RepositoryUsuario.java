package repository;

import lombok.Getter;
import lombok.Setter;
import persona.roles.Usuario;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class RepositoryUsuario {
    private static RepositoryUsuario instancia = null;
    @Setter private List<Usuario> usuarios;

    private RepositoryUsuario() {
        this.usuarios = Collections.synchronizedList(new ArrayList<>());
    }

    public static synchronized RepositoryUsuario getInstance() {
        if(instancia == null){
            instancia = new RepositoryUsuario();
        }
        return instancia;
    }

    public synchronized void addUsuario(Usuario usuario) {
        usuarios.add(usuario);
    }

    public boolean usernameExistente(String username) {
        return usuarios.stream().anyMatch(usuario -> usuario.getUsername().equals(username))
                || username.contains("administrador") || username.contains("admin");
    }
}