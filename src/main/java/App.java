import controller.LoginController;
import controller.ParticipanteController;
import controller.ProjetoController;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.template.JavalinThymeleaf;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import io.javalin.http.Context;

public class App {

    public void start() {
        // Configuração do Thymeleaf
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        // Configuração do Javalin
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "/templates";
                staticFileConfig.location = Location.CLASSPATH;
            });
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
        }).start(7000);


        app.get("/", ctx -> ctx.redirect("/login"));


        // Registra as rotas nos controladores
        registerRoutes(app);
        // Middleware para verificar autenticação em rotas protegidas
        app.before("/projetos/*", this::verificarAutenticacao);
        app.before("/participantes/*", this::verificarAutenticacao);
    }

    private void registerRoutes(Javalin app) {
        // Instancia os controladores
        ProjetoController projetoController = new ProjetoController();
        ParticipanteController participanteController = new ParticipanteController();
        LoginController loginController = new LoginController();

        // Registra as rotas de projeto
        projetoController.registerRoutes(app);
        // Registra as rotas de participante
        participanteController.registerRoutes(app);
        loginController.registerRouters(app);
    }

    private void verificarAutenticacao(Context ctx) {
        // Middleware para verificar autenticação
        String usuario = ctx.cookie("auth");
        if (usuario == null || usuario.isEmpty()) {
            ctx.redirect("/login");
        }
    }

    public static void main(String[] args) {
        new App().start();
    }
}
