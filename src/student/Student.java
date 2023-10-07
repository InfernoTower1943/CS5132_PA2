package student;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.time.Instant;
import java.util.*;

import javafx.util.Pair;
import main.Main;
import main.ModulePriorityQueue;

public class Student extends Application{
    private Stage globalStage;

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleDetails = new HashMap<>();
    public static Map<String, String> moduleDescriptions = new HashMap<>();
    public static SortedSet<String> studentsRequiredModules = new TreeSet<String>();
    public static Map<Pair<String, String>, Integer> timeVacancy= new HashMap<>();
    public static Map<Pair<String, String>, Integer> timeTotal= new HashMap<>();
    public static String studentID;
    public static String currentModuleCode;
    public static String currentTimeSlot;
    public static Integer currentTimeSlotID;
    public static boolean required;
    public static int preference;


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
    Label studentPreferenceLabel;
    @FXML
    ComboBox studentPreferenceComboBox;

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
    @FXML
    Button logoutButton;
    @FXML
    Button signUpButton;


    @Override
    public void start(Stage primaryStage) throws Exception {
        globalStage = primaryStage;
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
            timeVacancy.put(new Pair<String, String>(args[0],
                            modulePQ.timeSlotDescriptionMap.get(new Pair<>(args[0], Integer.parseInt(args[1])))),
                    Integer.parseInt(args[3].strip()));
            timeTotal.put(new Pair<String, String>(args[0],
                            modulePQ.timeSlotDescriptionMap.get(new Pair<>(args[0], Integer.parseInt(args[1])))),
                    Integer.parseInt(args[4].strip()));
            System.out.println(timeVacancy.get(new Pair<>(args[0], modulePQ.timeSlotDescriptionMap.get(new Pair<>(args[0], Integer.parseInt(args[1]))))));

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
                    studentsRequiredModules.add(args[i]);
                    studentRequiredModulesListView.getItems().add(args[i]);
                }
            }
        }
        scanner.close();

        ListView studentModulesAvailableListView = (ListView) loader.getNamespace().get("studentModulesAvailableListView");
        studentModulesAvailableListView.getItems().addAll(moduleSet);

        studentAvailableTimeSlotsComboBox = (ComboBox) loader.getNamespace().get("studentAvailableTimeSlotsComboBox");
        studentPreferenceLabel = (Label) loader.getNamespace().get("studentPreferenceLabel");
        studentPreferenceComboBox = (ComboBox) loader.getNamespace().get("studentPreferenceComboBox");

        studentModuleTitleLabel = (Label) loader.getNamespace().get("studentModuleTitleLabel");
        studentModuleCodeLabel = (Label) loader.getNamespace().get("studentModuleCodeLabel");
        studentVacanciesLabel = (Label) loader.getNamespace().get("studentVacanciesLabel");
        moduleDescriptionTextBox = (TextArea) loader.getNamespace().get("moduleDescriptionTextBox");
        studentRequiredModulesListView = (ListView) loader.getNamespace().get("studentRequiredModulesListView");
        logoutButton = (Button) loader.getNamespace().get("logoutButton");
        signUpButton = (Button) loader.getNamespace().get("signUpButton");

        signUpButton.setDisable(true);
        // TODO: If have previously stored data, load it and prevent user from editing.

        studentModulesAvailableListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        //System.out.println("Selected Item: " + newValue);
                        // Update choice box
                        String moduleCode = (String) newValue;
                        studentAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(moduleCode)){
                            studentAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(moduleCode, timeSlotID));
                        }
                        // set module title label
                        currentModuleCode=moduleCode;
                        studentModuleTitleLabel.setText(moduleDetails.get(moduleCode));
                        // set module code label
                        studentModuleCodeLabel.setText(moduleCode);
                        // set module description
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(moduleCode));
                        required = studentsRequiredModules.contains(currentModuleCode);
                    }
                });

        if (required){
            preference = 100;
            studentPreferenceLabel.setVisible(false);
            studentPreferenceComboBox.setVisible(false);
        } else {
            preference = 0;
            studentPreferenceLabel.setVisible(true);
            studentPreferenceComboBox.setVisible(true);
        }

        // TODO: Based on available preferences, load the data for studentPreferenceComboBox

        studentAvailableTimeSlotsComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !studentAvailableTimeSlotsComboBox.getSelectionModel().isEmpty()) {
                        Pair<String, Integer> timeSlot = modulePQ.getTimeSlotIDFromStr((String) (studentAvailableTimeSlotsComboBox.getItems().get((Integer) newValue)));
                        currentTimeSlot=timeSlot.getKey();
                        currentTimeSlotID=timeSlot.getValue();
                        int vacancyNumber = timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID))));
                        if (vacancyNumber <= 0) studentVacanciesLabel.setText("Full!");
                        else studentVacanciesLabel.setText("Available");
                        signUpButton.setDisable(false);
                        //System.out.println("Selected Time Slot: " + timeSlot.toString());
                    }
                }
        );

        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                modulePQ.enqueueToTimeSlot(currentModuleCode,currentTimeSlotID,studentID,modulePQ.getPriority(preference, timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID))))));//studentsRequiredModules.contains(currentModuleCode),1,Instant.now().toEpochMilli()));
                signUpButton.setDisable(true);
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

        primaryStage.setTitle("student");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
