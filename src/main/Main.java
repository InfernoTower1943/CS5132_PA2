package main;

import admin.Admin;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
    public static Map<String, String> studentsLogin = new HashMap<>();
    public static Map<String, String> adminsLogin = new HashMap<>();

    //@FXML Parent loginViewRoot;
    @FXML
    Button loginButton;
    @FXML
    TextField usernameTextBox;
    @FXML
    PasswordField passwordTextBox;

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

        // student and Admin username and password file input
        scanner = new Scanner(new File("StudentLoginDetails.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            studentsLogin.put(args[0], args[1]);
        }
        scanner.close();

        scanner = new Scanner(new File("AdminLoginDetails.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            adminsLogin.put(args[0], args[1]);
        }
        scanner.close();

        // TODO: username and password verification
        loginButton = (Button) loader.getNamespace().get("loginButton");
        usernameTextBox = (TextField) loader.getNamespace().get("usernameTextBox");
        passwordTextBox = (PasswordField) loader.getNamespace().get("passwordTextBox");

        loginButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                if (studentsLogin.containsKey(usernameTextBox.getText()) &&
                        studentsLogin.get(usernameTextBox.getText()).equals(passwordTextBox.getText())) {
                    try {
                        new Student().start(globalStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (adminsLogin.containsKey(usernameTextBox.getText()) &&
                        adminsLogin.get(usernameTextBox.getText()).equals(passwordTextBox.getText())) {
                    try {
                        new Admin().start(globalStage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        globalStage.setTitle("Module Registration System");
        globalStage.setScene(scene);
        globalStage.show();


    }
}
