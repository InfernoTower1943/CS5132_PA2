import Student.StudentEditChoicesController;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

public class Main extends Application {
    public static void main(String[] args) {
        Application.launch(args);
    }

    @FXML
    private void studentEditChoicesButtonOnClick(ActionEvent event) throws Exception {
        event.consume();
        new StudentEditChoicesController().start(new Stage());
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("Student/StudentView.fxml"));
        Parent root = loader.load();

        Scene scene = new Scene(root, 1200, 800);

        // Initialisation of Priority Queue and Module List
        ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<>();
        SortedSet<String> moduleSet = new TreeSet<String>();

        Scanner scanner = new Scanner(new File("ModulesAndTimeSlots.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            modulePQ.addTimeSlot(args[0], Integer.parseInt(args[1]), args[2]);
            moduleSet.add(args[0]);
        }
        scanner.close();

        ListView studentModulesAvailableListView = (ListView) loader.getNamespace().get("studentModulesAvailableListView");
        studentModulesAvailableListView.getItems().addAll(moduleSet);

        ChoiceBox studentAvailableTimeSlotsChoiceBox =
                (ChoiceBox) loader.getNamespace().get("studentAvailableTimeSlotsChoiceBox");


        primaryStage.setTitle("Module Registration");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
