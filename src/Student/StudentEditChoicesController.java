package Student;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class StudentEditChoicesController extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Student/StudentEditChoicePopupView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 400);

        primaryStage.setTitle("Module Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
