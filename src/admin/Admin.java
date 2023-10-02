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
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.util.*;

import main.Main;
import main.ModulePriorityQueue;
import student.Student;
import student.StudentEditChoicesController;

public class Admin extends Application{
    private Stage globalStage;

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleDetails = new HashMap<>();
    public static Map<String, String> moduleDescriptions = new HashMap<>();
    public static Map<String, Integer> timeVacancy= new HashMap<>();
    public static Map<String, Integer> timeTotal= new HashMap<>();


    public static SortedSet<String> studentSet = new TreeSet<String>();
    public static Map<String, SortedSet<String>> studentsRequiredModules = new HashMap<>();
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
    @FXML
    ListView adminRequiredModulesListView;
    @FXML
    Button requiredModulesEditButton;
    @FXML
    Button requiredModulesSaveButton;
    @FXML
    Button logoutButton;

    @Override
    public void start(Stage primaryStage) throws Exception {
        globalStage = primaryStage;
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
            timeVacancy.put(args[2],Integer.parseInt(args[3]));
            timeTotal.put(args[2],Integer.parseInt(args[4].strip()));
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
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] args = line.split(",", 3);
            moduleDetails.put(args[0], args[1]);
            moduleDescriptions.put(args[0], args[2]);
        }
        scanner.close();

        scanner = new Scanner(new File("StudentRequiredModules.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",");
            if (!studentsRequiredModules.containsKey(args[0])) {
                studentsRequiredModules.put(args[0], new TreeSet<String>());
            }
            for (int i=1; i<args.length; i++) {
                studentsRequiredModules.get(args[0]).add(args[i]);
            }
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
        adminRequiredModulesListView = (ListView) loader.getNamespace().get("adminRequiredModulesListView");


        adminModuleTitleTextBox = (TextField) loader.getNamespace().get("adminModuleTitleTextBox");
        adminModuleCodeTextBox = (TextField) loader.getNamespace().get("adminModuleCodeTextBox");
        // TODO: make total spots text box numeric only
        adminTotalSpotsTextBox = (TextField) loader.getNamespace().get("adminTotalSpotsTextBox");
        moduleDescriptionTextBox = (TextArea) loader.getNamespace().get("moduleDescriptionTextBox");
        timeSlotEditButton = (Button) loader.getNamespace().get("timeSlotEditButton");
        timeSlotSaveButton = (Button) loader.getNamespace().get("timeSlotSaveButton");
        descriptionEditButton = (Button) loader.getNamespace().get("descriptionEditButton");
        descriptionSaveButton = (Button) loader.getNamespace().get("descriptionSaveButton");
        requiredModulesEditButton = (Button) loader.getNamespace().get("requiredModulesEditButton");
        requiredModulesSaveButton = (Button) loader.getNamespace().get("requiredModulesSaveButton");
        logoutButton = (Button) loader.getNamespace().get("logoutButton");

        adminAllModulesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {

                        // TODO: make student details VBox invisible and expand the module details VBox pane somehow
                        //mainSplitPane.getItems().remove(studentDetailsVBox);
                        moduleDetailsVBox.setVisible(true);
                        studentDetailsVBox.setVisible(false);
                        //mainSplitPane.setDividerPosition();


                        // Update choice box
                        selectedModule = (String) newValue;
                        adminAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(selectedModule)){
                            adminAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(selectedModule, timeSlotID));
                        }
                        // set module title label
                        adminModuleTitleTextBox.setText(moduleDetails.get(selectedModule));
                        // set module code label
                        adminModuleCodeTextBox.setText(selectedModule);
                        // set module description
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(selectedModule));
                    }
                });

        adminAllStudentsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {

                        // TODO: make modules details VBox invisible and expand the student details VBox pane somehow
                        moduleDetailsVBox.setVisible(false);
                        studentDetailsVBox.setVisible(true);

                        String studentID = (String) newValue;
                        adminRequiredModulesListView.getItems().clear();

                        SortedSet<String> required = studentsRequiredModules.get(studentID);
                        for (String moduleCode : moduleSet){
                            CheckBox cb = new CheckBox(moduleCode);
                            if (required.contains(moduleCode)){
                                cb.setSelected(true);
                            }
                            adminRequiredModulesListView.getItems().add(cb);
                        }
                        //adminRequiredModulesListView.getItems().addAll();
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
                moduleDetails.remove(selectedModule);
                String title=adminModuleTitleTextBox.getText();
                String code=adminModuleCodeTextBox.getText();
                moduleDetails.put(code,title);
                moduleDescriptions.put(code,moduleDescriptions.get(selectedModule));
                moduleDescriptions.remove(selectedModule);
                moduleSet.remove(selectedModule);
                moduleSet.add(code);
                adminAllModulesListView.getItems().clear();
                adminAllModulesListView.getItems().addAll(moduleSet);
                ArrayList<Integer> list =new ArrayList<>(modulePQ.getTimeSlotIDs(selectedModule));
                for (int num:list){
                    String description=modulePQ.getTimeSlot(selectedModule,num);
                    modulePQ.deleteTimeSlot(selectedModule,num);
                    modulePQ.addTimeSlot(code,num,description);
                }
                try {
                    write();
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
                String description=moduleDescriptionTextBox.getText();
                moduleDescriptions.put(selectedModule,description);
                try {
                    write();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        requiredModulesEditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminRequiredModulesListView.setDisable(false);
                requiredModulesEditButton.setDisable(true);
                requiredModulesSaveButton.setDisable(false);
            }
        });

        requiredModulesSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminRequiredModulesListView.setDisable(true);

                requiredModulesEditButton.setDisable(false);
                requiredModulesSaveButton.setDisable(true);
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    new Main().start(globalStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        primaryStage.setTitle("admin");
        primaryStage.setScene(scene);
        primaryStage.show();


    }

    private static void write() throws IOException {
        FileWriter outputStream = new FileWriter("ModuleDetails.txt");
        FileWriter outputStream1 = new FileWriter("ModulesAndTimeSlots.txt");
        for (String string:moduleSet) {
            outputStream.write(string + "," + moduleDetails.get(string) + "," + moduleDescriptions.get(string)+"\n");
            ArrayList<Integer> list = new ArrayList<>(modulePQ.getTimeSlotIDs(string));
            for (int num : list) {
                String description = modulePQ.getTimeSlot(string, num);
                outputStream1.write(string+","+num+","+description+","+timeVacancy.get(description)+','+timeTotal.get(description)+"\n");
            }
        }
        outputStream.close();
        outputStream1.close();
    }
}
