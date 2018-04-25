import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout_main.fxml"));
        Parent root = (Parent) loader.load();
        Controller controller = (Controller) loader.getController();
        primaryStage.setTitle("Diffraction");
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.updateUI(); // render the graphs and stuff after the window has loaded
    }

    public static void main(String[] args) {
        launch(args);
    }
}
