package student;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

public class StudentEditChoicesController extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Student/StudentEditChoicePopupView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 300, 400);

        Button loginButton = (Button) loader.getNamespace().get("loginButton");
        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                actionEvent.consume();
                try {
                    new StudentEditChoicesController().start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setTitle("Module Registration");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
