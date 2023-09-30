package admin;

import com.sun.source.tree.Tree;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.util.*;

import main.ModulePriorityQueue;
import student.Student;
import student.StudentEditChoicesController;

public class Admin extends Application{

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleDetails = new HashMap<>();
    public static Map<String, String> moduleDescriptions = new HashMap<>();

    public static SortedSet<String> studentSet = new TreeSet<String>();
    public static String selectedModule = "";
    public static void main(String[] args) {
        Application.launch(args);
    }

    @FXML
    SplitPane mainSplitPane;
    @FXML
    VBox moduleDetailsVBox;
    @FXML
    VBox studentDetailsVBox;


    @FXML
    ListView adminAllModulesListView;
    @FXML
    ListView adminAllStudentsListView;

    @FXML
    ComboBox adminAvailableTimeSlotsComboBox;
    @FXML
    TextField adminModuleTitleTextBox;
    @FXML
    TextField adminModuleCodeTextBox;
    @FXML
    TextField adminTotalSpotsTextBox;
    @FXML
    TextArea moduleDescriptionTextBox;
    @FXML
    Button timeSlotEditButton;
    @FXML
    Button timeSlotSaveButton;
    @FXML
    Button descriptionEditButton;
    @FXML
    Button descriptionSaveButton;


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
            String[] args = line.split(",", 3);
            moduleDetails.put(args[0], args[1]);
            moduleDescriptions.put(args[0], args[2]);
        }
        scanner.close();

        mainSplitPane = (SplitPane) loader.getNamespace().get("mainSplitPane");

        moduleDetailsVBox = (VBox) loader.getNamespace().get("moduleDetailsVBox");
        studentDetailsVBox = (VBox) loader.getNamespace().get("studentDetailsVBox");


        adminAllModulesListView = (ListView) loader.getNamespace().get("adminAllModulesListView");
        adminAllModulesListView.getItems().addAll(moduleSet);
        adminAvailableTimeSlotsComboBox = (ComboBox) loader.getNamespace().get("adminAvailableTimeSlotsComboBox");

        adminAllStudentsListView = (ListView) loader.getNamespace().get("adminAllStudentsListView");
        adminAllStudentsListView.getItems().addAll(studentSet);


        adminModuleTitleTextBox = (TextField) loader.getNamespace().get("adminModuleTitleTextBox");
        adminModuleCodeTextBox = (TextField) loader.getNamespace().get("adminModuleCodeTextBox");
        // TODO: make total spots text box numeric only
        adminTotalSpotsTextBox = (TextField) loader.getNamespace().get("adminTotalSpotsTextBox");
        moduleDescriptionTextBox = (TextArea) loader.getNamespace().get("moduleDescriptionTextBox");
        timeSlotEditButton = (Button) loader.getNamespace().get("timeSlotEditButton");
        timeSlotSaveButton = (Button) loader.getNamespace().get("timeSlotSaveButton");
        descriptionEditButton = (Button) loader.getNamespace().get("descriptionEditButton");
        descriptionSaveButton = (Button) loader.getNamespace().get("descriptionSaveButton");

        adminAllModulesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {

                        // TODO: make student details VBox invisible and expand the module details VBox pane somehow
                        //mainSplitPane.getItems().remove(studentDetailsVBox);
                        studentDetailsVBox.setVisible(false);
                        //mainSplitPane.setDividerPosition();


                        // Update choice box
                        String moduleCode = (String) newValue;
                        adminAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(moduleCode)){
                            adminAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(moduleCode, timeSlotID));
                        }
                        // set module title label
                        adminModuleTitleTextBox.setText(moduleDetails.get(moduleCode));
                        // set module code label
                        adminModuleCodeTextBox.setText(moduleCode);
                        // set module description
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(moduleCode));
                    }
                });

        timeSlotEditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminModuleTitleTextBox.setEditable(true);
                adminModuleCodeTextBox.setEditable(true);
                adminTotalSpotsTextBox.setEditable(true);
                timeSlotEditButton.setDisable(true);
                timeSlotSaveButton.setDisable(false);
            }
        });

        timeSlotSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminModuleTitleTextBox.setEditable(false);
                adminModuleCodeTextBox.setEditable(false);
                adminTotalSpotsTextBox.setEditable(false);
                timeSlotEditButton.setDisable(false);
                timeSlotSaveButton.setDisable(true);
            }
        });

        descriptionEditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moduleDescriptionTextBox.setEditable(true);
                descriptionEditButton.setDisable(true);
                descriptionSaveButton.setDisable(false);
            }
        });

        descriptionSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                moduleDescriptionTextBox.setEditable(false);
                descriptionEditButton.setDisable(false);
                descriptionSaveButton.setDisable(true);
            }
        });

        primaryStage.setTitle("admin");
        primaryStage.setScene(scene);
        primaryStage.show();


    }
}
