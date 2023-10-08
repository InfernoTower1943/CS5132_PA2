package admin;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.Window;

public class PQView extends Application {
    @FXML
    Button removeTopButton;
    @FXML
    Label PQmoduleTitleLabel, PQmoduleCodeLabel, PQtimeSlotLabel, PQstudentIDLabel, PQprefLabel;

    public static String moduleTitle, moduleCode, timeSlot, studentID, pref;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Admin/PQView.fxml"));
        Parent root = loader.load();

        removeTopButton = (Button) loader.getNamespace().get("removeTopButton");
        PQmoduleTitleLabel = (Label) loader.getNamespace().get("PQmoduleTitleLabel");
        PQmoduleCodeLabel = (Label) loader.getNamespace().get("PQmoduleCodeLabel");
        PQtimeSlotLabel = (Label) loader.getNamespace().get("PQtimeSlotLabel");
        PQstudentIDLabel = (Label) loader.getNamespace().get("PQstudentIDLabel");
        PQprefLabel = (Label) loader.getNamespace().get("PQprefLabel");

        PQmoduleTitleLabel.setText(moduleTitle);
        PQmoduleCodeLabel.setText(moduleCode);
        PQtimeSlotLabel.setText(timeSlot);
        PQstudentIDLabel.setText(studentID);
        PQprefLabel.setText(pref);

        removeTopButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Admin.removePQTop();
                Window window = removeTopButton.getScene().getWindow();

                if (window instanceof Stage){
                    ((Stage) window).close();
                }
            }
        });

        Scene scene = new Scene(root, 640, 400);

        primaryStage.setTitle("View Priority Queue");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
