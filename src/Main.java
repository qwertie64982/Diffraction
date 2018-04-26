/*
 * Diffraction simulation
 *
 * Maxwell Sherman, Malik Al Ali, Daniel Cole
 * Spring 2018, CSCI 2300-M01
 */

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main UI class (only GUI, no command-line)
 */
public class Main extends Application {

    /**
     * UI start method
     * @param primaryStage stage (window) where everything will be drawn
     * @throws Exception inherited
     */
    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("layout_main.fxml"));
        Parent root = (Parent) loader.load();
        Controller controller = (Controller) loader.getController();
        primaryStage.setTitle("Diffraction");
        Scene scene = new Scene(root, 1000, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
        controller.updateUI(); // Render the graphs and stuff after the window has loaded
    }

    /**
     * Command-line start method (just starts the UI)
     * @param args command-line arguments - not used
     */
    public static void main(String[] args) {
        launch(args);
    }
}
