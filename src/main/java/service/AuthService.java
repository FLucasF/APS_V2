package service;

import model.Usuario;
import repository.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Optional;

public class AuthService {

    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    public boolean autenticar(String username, String password) {
        Optional<Usuario> usuarioOpt = usuarioRepository.buscarPorUsername(username);

        if (usuarioOpt.isPresent()) {
            Usuario usuario = usuarioOpt.get();
            System.out.println("Usuário encontrado: " + usuario.getLogin());
            System.out.println("Senha digitada: " + password);
            System.out.println("Hash salvo no banco: " + usuario.getSenha());

            boolean senhaCorreta = BCrypt.checkpw(password, usuario.getSenha());
            System.out.println("Senha correta? " + senhaCorreta);

            return senhaCorreta;
        }

        System.out.println("Usuário não encontrado.");
        return false;
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
