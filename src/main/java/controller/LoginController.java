package controller;

import io.javalin.Javalin;
import io.javalin.http.Context;
import service.AuthService;

import java.util.Map;

public class LoginController {

    private final AuthService authService = new AuthService();

    public void registerRouters(Javalin app) {
        app.get("/login", this::mostrarPaginaLogin);
        app.post("/login", this::processarLogin);

        app.get("/register", this::mostrarPaginaRegistro);
        app.post("/register", this::processarRegistro);

        app.get("/logout", this::logout);

    }

    private void mostrarPaginaLogin(Context ctx) {
        System.out.println("Exibindo página de login");
        ctx.render("login/login.html");
    }

    private void processarLogin(Context ctx) {
        System.out.println("Processando login");

        String username = ctx.formParam("username");
        String password = ctx.formParam("password");

        if (username == null || password == null) {
            ctx.status(400).result("Usuário e senha são obrigatórios.");
            return;
        }

        if (authService.autenticar(username, password)) {
            // Define um cookie seguro após o login
            ctx.cookie("auth", username, 3600); // Expira em 1 hora
            System.out.println("Login bem-sucedido, redirecionando para /projetos/listar");
            ctx.redirect("/projetos/listar");
        } else {
            ctx.status(401).render("login/login.html", Map.of("error", "Usuário ou senha inválidos."));
        }
    }

    private void mostrarPaginaRegistro(Context ctx) {
        System.out.println("Exibindo página de registro");
        ctx.render("login/register.html");
    }

    private void processarRegistro(Context ctx) {
        System.out.println("Processando registro de novo usuário");

        String login = ctx.formParam("login");
        String nome = ctx.formParam("nome");
        String password = ctx.formParam("password");

        if (login == null || nome == null || password == null) {
            System.out.println("Erro: Login, nome ou password estão nulos.");
            ctx.status(400).result("Login, nome e senha são obrigatórios.");
            return;
        }

        try {
            authService.registrarUsuario(login, nome, password);
            System.out.println("Usuário registrado com sucesso: " + login);
            ctx.redirect("/login");
        } catch (Exception e) {
            System.out.println("Erro ao registrar usuário: " + e.getMessage());
            ctx.status(500).result("Erro ao registrar usuário.");
        }
    }

    private void logout(Context ctx) {
        System.out.println("Processando logout");
        ctx.removeCookie("auth");
        ctx.redirect("/login");
    }
}
