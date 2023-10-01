package student;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

import javafx.util.Pair;
import main.ModulePriorityQueue;

public class Student extends Application{

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleDetails = new HashMap<>();
    public static Map<String, String> moduleDescriptions = new HashMap<>();
    public static String studentID;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @FXML
    private void studentEditChoicesButtonOnClick(ActionEvent event) throws Exception {
        event.consume();
        new StudentEditChoicesController().start(new Stage());
    }

    @FXML
    ComboBox studentAvailableTimeSlotsComboBox;

    @FXML
    Label studentModuleTitleLabel;
    @FXML
    Label studentModuleCodeLabel;
    @FXML
    Label studentVacanciesLabel;
    @FXML
    TextArea moduleDescriptionTextBox;
    @FXML
    ListView studentRequiredModulesListView;

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getClassLoader().getResource("student/StudentView.fxml"));
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


        scanner = new Scanner(new File("ModuleDetails.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",", 3);
            moduleDetails.put(args[0], args[1]);
            moduleDescriptions.put(args[0], args[2]);
        }
        scanner.close();


        studentRequiredModulesListView = (ListView) loader.getNamespace().get("studentRequiredModulesListView");
        scanner = new Scanner(new File("StudentRequiredModules.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",");
            if (args[0].equals(studentID)){
                for (int i=1; i<args.length; i++) {
                    studentRequiredModulesListView.getItems().add(args[i]);
                }
            }
        }
        scanner.close();

        ListView studentModulesAvailableListView = (ListView) loader.getNamespace().get("studentModulesAvailableListView");
        studentModulesAvailableListView.getItems().addAll(moduleSet);

        studentAvailableTimeSlotsComboBox = (ComboBox) loader.getNamespace().get("studentAvailableTimeSlotsComboBox");

        studentModuleTitleLabel = (Label) loader.getNamespace().get("studentModuleTitleLabel");
        studentModuleCodeLabel = (Label) loader.getNamespace().get("studentModuleCodeLabel");
        studentVacanciesLabel = (Label) loader.getNamespace().get("studentVacanciesLabel");
        moduleDescriptionTextBox = (TextArea) loader.getNamespace().get("moduleDescriptionTextBox");
        studentRequiredModulesListView = (ListView) loader.getNamespace().get("studentRequiredModulesListView");

        studentModulesAvailableListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        System.out.println("Selected Item: " + newValue);
                        // Update choice box
                        String moduleCode = (String) newValue;
                        studentAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(moduleCode)){
                            studentAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(moduleCode, timeSlotID));
                        }
                        // set module title label
                        studentModuleTitleLabel.setText(moduleDetails.get(moduleCode));
                        // set module code label
                        studentModuleCodeLabel.setText(moduleCode);
                        // set module description
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(moduleCode));
                    }
                });

        studentAvailableTimeSlotsComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    Pair<String, Integer> timeSlot = modulePQ.getTimeSlotIDFromStr((String)(studentAvailableTimeSlotsComboBox.getItems().get((Integer) newValue)));
                    System.out.println("Selected Time Slot: " +  timeSlot.toString());
                    // TODO: set vacancies label
                }
        );

        primaryStage.setTitle("student");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
