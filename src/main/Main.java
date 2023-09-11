package main;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import student.Student;
import student.StudentEditChoicesController;

import java.io.File;
import java.util.*;

public class Main extends Application {
    private Stage globalStage;
    public static void main(String[] args) {
        Application.launch(args);
    }

    // Initialisation of Priority Queue and Module List
    public static ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();

    //@FXML Parent loginViewRoot;
    @FXML
    Button loginButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        globalStage = primaryStage;
        FXMLLoader loader = new FXMLLoader(getClass().getResource("LoginView.fxml"));
        Parent root = loader.load();
        //Parent root = loginViewRoot;

        Scene scene = new Scene(root, 1200, 800);

        Scanner scanner = new Scanner(new File("ModulesAndTimeSlots.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            modulePQ.addTimeSlot(args[0], Integer.parseInt(args[1]), args[2]);
            moduleSet.add(args[0]);
        }
        scanner.close();

        // TODO: student and teacher username and password file input

        // TODO: username and password verification

        loginButton = (Button) loader.getNamespace().get("loginButton");

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                try {
                    new Student().start(globalStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        globalStage.setTitle("Module Registration System");
        globalStage.setScene(scene);
        globalStage.show();


    }
}
