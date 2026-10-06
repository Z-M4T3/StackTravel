package hu.unideb.inf;

import hu.unideb.inf.config.DotenvConfig;
import hu.unideb.inf.config.JpaConfig;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Main extends Application {
    private AnnotationConfigApplicationContext context;

    @Override
    public void init() {
        DotenvConfig.load();
        this.context = new AnnotationConfigApplicationContext(JpaConfig.class);
    }
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(Main.class.getResource("/fxml/FXMLStudentsScene.fxml"));
        loader.setControllerFactory(context::getBean);
        Scene scene = new Scene(loader.load());
        stage.setTitle("Students Register");
        stage.setScene(scene);
        stage.show();

    }
    @Override
    public void stop() {
        if (context != null) {
            context.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
