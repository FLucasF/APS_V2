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
        //config do Thymeleaf
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setPrefix("/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setTemplateMode("HTML");
        templateResolver.setCharacterEncoding("UTF-8");

        TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        //config do Javalin
        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFileConfig -> {
                staticFileConfig.directory = "/templates";
                staticFileConfig.location = Location.CLASSPATH;
            });
            config.fileRenderer(new JavalinThymeleaf(templateEngine));
        }).start(7000);

        registerRoutes(app);

        app.get("/", ctx -> ctx.redirect("/login"));
        app.before("/projetos/*", this::verificarAutenticacao); //posso simplificar com apenas "/"? ou é um mal hábito?
        app.before("/participantes/*", this::verificarAutenticacao);
    }

    private void registerRoutes(Javalin app) {
        ProjetoController projetoController = new ProjetoController();
        ParticipanteController participanteController = new ParticipanteController();
        LoginController loginController = new LoginController();

        projetoController.registerRoutes(app);
        participanteController.registerRoutes(app);
        loginController.registerRouters(app);
    }

    //middleware
    private void verificarAutenticacao(Context ctx) {
        String usuario = ctx.cookie("auth");
        if (usuario == null || usuario.isEmpty()) {
            ctx.redirect("/login");
        }
    }

    public static void main(String[] args) {
        new App().start();
    }
}
