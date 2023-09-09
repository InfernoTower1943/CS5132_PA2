import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class Main extends Application{
    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("HBox Experiment 1");

        Button button = new Button("My Button");
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("StudentView.fxml"));

        Scene scene = new Scene(root, 1200, 800);

        primaryStage.setTitle("Module Registration");
        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
