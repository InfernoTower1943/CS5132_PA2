package Student;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Student {

    public void showChoicePopup() throws Exception {

        Stage stage = new Stage();
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("../StudentEditChoicePopupView.fxml"));

        Scene scene = new Scene(root, 300, 400);

        stage.setTitle("Select Module Choices");
        stage.setScene(scene);
        stage.show();

    }
}
