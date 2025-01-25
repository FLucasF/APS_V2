package service;

import model.Usuario;
import repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorUsername(username);
        return usuarioOpt.filter(usuario -> BCrypt.checkpw(password, usuario.getSenha())).isPresent();
    }

    public void registrarUsuario(String login, String nome, String password) {
        String passwordHash = BCrypt.hashpw(password, BCrypt.gensalt());
        Usuario usuario = new Usuario();
        usuario.setLogin(login);
        usuario.setNome(nome);
        usuario.setSenha(passwordHash);

        usuarioRepository.adicionarUsuario(usuario);
    }
}
