package admin;

import com.sun.source.tree.Tree;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

import main.ModulePriorityQueue;
import student.StudentEditChoicesController;

public class Admin extends Application{

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleDetails = new HashMap<>();

    public static SortedSet<String> studentSet = new TreeSet<String>();
    public static String selectedModule = "";
    public static void main(String[] args) {
        Application.launch(args);
    }

    @FXML
    ListView adminAllModulesListView;
    @FXML
    ListView adminAllStudentsListView;
    @FXML
    TextField adminModuleTitleTextBox;
    @FXML
    TextField adminModuleCodeTextBox;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("admin/AdminView.fxml"));
        Parent root = loader.load();

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
        scanner = new Scanner(new File("StudentLoginDetails.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            studentSet.add(args[0]);
        }
        scanner.close();

        scanner = new Scanner(new File("ModuleDetails.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",");
            moduleDetails.put(args[0], args[1]);
        }
        scanner.close();

        adminAllModulesListView = (ListView) loader.getNamespace().get("adminAllModulesListView");
        adminAllModulesListView.getItems().addAll(moduleSet);
        ChoiceBox adminAvailableTimeSlotsChoiceBox =
                (ChoiceBox) loader.getNamespace().get("adminAvailableTimeSlotsChoiceBox");

        adminAllStudentsListView = (ListView) loader.getNamespace().get("adminAllStudentsListView");
        adminAllStudentsListView.getItems().addAll(studentSet);

        adminModuleTitleTextBox = (TextField) loader.getNamespace().get("adminModuleTitleTextBox");
        adminModuleCodeTextBox = (TextField) loader.getNamespace().get("adminModuleCodeTextBox");

        adminAllModulesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        System.out.println("Selected Item: " + newValue);
                        // Update choice box
                        String moduleCode = (String) newValue;
                        adminAvailableTimeSlotsChoiceBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(moduleCode)){
                            adminAvailableTimeSlotsChoiceBox.getItems().add(modulePQ.getTimeSlot(moduleCode, timeSlotID));
                        }
                        // set module title label
                        adminModuleTitleTextBox.setText(moduleDetails.get(moduleCode));
                        // set module code label
                        adminModuleCodeTextBox.setText(moduleCode);
                    }
                });

        primaryStage.setTitle("admin");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
