package student;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
    public static Map<String, Pair<Integer, Integer>> registeredModules;
    public static boolean registered;
    public static boolean vacant;
    public static ArrayList<Integer> availablePreferences;
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
    Label requiredLabel;
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
        reset();

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
        }
        scanner.close();

        scanner = new Scanner(new File("ModuleDetails.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",", 4);
            moduleDetails.put(args[0], args[1]);
            moduleDescriptions.put(args[0], args[2]);
        }
        scanner.close();

        // allow for up to 5 additional non-compulsory modules to be taken
        availablePreferences = new ArrayList<>();
        availablePreferences.add(1);
        availablePreferences.add(2);
        availablePreferences.add(3);
        availablePreferences.add(4);
        availablePreferences.add(5);

        scanner = new Scanner(new File("ModulePriorityQueue.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            String module=args[0];
            int timeSlotID=Integer.parseInt(args[1].strip());
            long count=Long.parseLong(args[2].strip());
            for (int i=0; i<count;i++){
                line = scanner.next();
                args = line.split(",");
                modulePQ.enqueueToTimeSlot(module,timeSlotID,args[0],Integer.parseInt(args[1].strip()));
                if (args[0].equals(studentID)){
                    int pref = 0;
                    if (Integer.parseInt(args[1].strip()) > 0) {
                        pref = ((Integer.parseInt(args[1].strip()) - 1) / 1000) + 1;
                        if (availablePreferences.contains(Integer.valueOf(pref))){
                            availablePreferences.remove(Integer.valueOf(pref));
                        }
                    }
                    if (!registeredModules.containsKey(module)) {
                        registeredModules.put(module, new Pair(timeSlotID, pref));
                    }

                }
            }
        }
        scanner.close();


        studentRequiredModulesListView = (ListView) loader.getNamespace().get("studentRequiredModulesListView");
        requiredLabel = (Label) loader.getNamespace().get("requiredLabel");
        requiredLabel.setVisible(false);
        scanner = new Scanner(new File("StudentRequiredModules.txt"));
        while(scanner.hasNextLine()){
            String line = scanner.nextLine();
            String[] args = line.split(",");
            if (args[0].equals(studentID)){
                for (int i=1; i<args.length; i++) {
                    studentsRequiredModules.add(args[i]);
                    studentRequiredModulesListView.getItems().add(args[i]);
                    boolean missed=true;
                    for (Integer integer:modulePQ.getTimeSlotIDs(args[i])){
                        if (modulePQ.checkPlaceInTimeSlot(args[i],integer,studentID)){
                            missed=false;
                        }
                    }
                    if (missed){
                        requiredLabel.setVisible(true);
                    }
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

        if (!check()){
            requiredLabel.setVisible(false);
        } else {
            requiredLabel.setVisible(true);
        }

        // TODO: If have previously stored data, load it and prevent user from editing.

        studentModulesAvailableListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    signUpButton.setDisable(true);

                    if (newValue != null) {
                        // Update choice box
                        String moduleCode = (String) newValue;
                        registered = registeredModules.containsKey(moduleCode);

                        studentAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(moduleCode)) {
                            studentAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(moduleCode, timeSlotID));
                        }

                        // Update preference box
                        studentPreferenceComboBox.getItems().clear();
                        for (Integer preference : availablePreferences) {
                            studentPreferenceComboBox.getItems().add(preference);
                        }

                        // set module title label
                        currentModuleCode = moduleCode;
                        studentModuleTitleLabel.setText(moduleDetails.get(moduleCode));
                        // set module code label
                        studentModuleCodeLabel.setText(moduleCode);
                        // set module description
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(moduleCode));
                        required = studentsRequiredModules.contains(currentModuleCode);

                        if (required) {
                            preference = 0;
                            studentPreferenceLabel.setVisible(false);
                            studentPreferenceComboBox.setVisible(false);
                        } else {
                            studentPreferenceLabel.setVisible(true);
                            studentPreferenceComboBox.setVisible(true);
                        }


                        if (!registered) {
                            studentAvailableTimeSlotsComboBox.setDisable(false);
                            studentPreferenceComboBox.setDisable(false);
                            studentPreferenceComboBox.valueProperty().set(null);
                        } else {
                            studentAvailableTimeSlotsComboBox.setDisable(true);
                            studentPreferenceComboBox.setDisable(true);
                            currentTimeSlotID = registeredModules.get(moduleCode).getKey();
                            studentAvailableTimeSlotsComboBox.valueProperty().set(modulePQ.getTimeSlot(moduleCode, currentTimeSlotID));
                            studentPreferenceComboBox.valueProperty().set(registeredModules.get(moduleCode).getValue());
                        }
                    }
                });

        studentAvailableTimeSlotsComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (registered){
                        signUpButton.setDisable(true);
                    } else if (newValue != null && !studentAvailableTimeSlotsComboBox.getSelectionModel().isEmpty()) {
                        Pair<String, Integer> timeSlot = modulePQ.getTimeSlotIDFromStr((String) (studentAvailableTimeSlotsComboBox.getItems().get((Integer) newValue)));
                        currentTimeSlot=timeSlot.getKey();
                        currentTimeSlotID=timeSlot.getValue();
                        int vacancyNumber = timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID))));
                        vacant = vacancyNumber > 0;
                        if (!vacant) { studentVacanciesLabel.setText("Full, Depends on preference"); signUpButton.setDisable(true);}
                        else {studentVacanciesLabel.setText("Available");}

                        if (required)
                            signUpButton.setDisable(false);
                        studentPreferenceComboBox.valueProperty().set(null);
                    }
                }
        );

        studentPreferenceComboBox.getSelectionModel().selectedIndexProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null && !studentPreferenceComboBox.getSelectionModel().isEmpty()) {
                        preference = (Integer) studentPreferenceComboBox.getItems().get((Integer) newValue); // timeslot selection should have been made
                        if (!vacant){
                            int vacancy = timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID))));
                            if (modulePQ.getModulePQ(currentModuleCode).get(currentTimeSlotID).peek() != null &&
                                    modulePQ.getModulePQ(currentModuleCode).get(currentTimeSlotID).peek() > modulePQ.getPriority(preference, vacancy)){
                                if (required || (!required && !studentAvailableTimeSlotsComboBox.getSelectionModel().isEmpty())) {
                                    signUpButton.setDisable(false);
                                    studentVacanciesLabel.setText("Available");
                                }
                                else{
                                    studentVacanciesLabel.setText("Full, Depends on preference");
                                }
                            }
                            else{
                                signUpButton.setDisable(true);
                                studentVacanciesLabel.setText("Full, Depends on preference");
                            }
                        }
                        else {
                            if (!studentAvailableTimeSlotsComboBox.getSelectionModel().isEmpty())
                                signUpButton.setDisable(false);
                        }
                    }
                }
        );

        signUpButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                int vacancy = timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID))));
                if (vacant || (modulePQ.getModulePQ(currentModuleCode).get(currentTimeSlotID).peek() != null && // if, full checks priority
                        modulePQ.getModulePQ(currentModuleCode).get(currentTimeSlotID).peek() > modulePQ.getPriority(preference, vacancy))){
                    if (!vacant)
                        modulePQ.dequeueFromTimeSlot(currentModuleCode, currentTimeSlotID);
                    modulePQ.enqueueToTimeSlot(currentModuleCode, currentTimeSlotID, studentID,
                            modulePQ.getPriority(preference, vacancy));
                    timeVacancy.put(new Pair<>(currentModuleCode, modulePQ.timeSlotDescriptionMap.get(
                            new Pair<>(currentModuleCode, currentTimeSlotID))),vacancy-1);
                    System.out.println(vacancy);
                    System.out.println(modulePQ.getPriority(preference, vacancy));
                    System.out.println(timeVacancy.get(new Pair<>(currentModuleCode,modulePQ.timeSlotDescriptionMap.get(new Pair<>(currentModuleCode, currentTimeSlotID)))));
                    availablePreferences.remove(Integer.valueOf(preference));
                    registeredModules.put(currentModuleCode, new Pair(currentTimeSlotID, preference));
                    try {
                        write();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    timeVacancy.put(
                            new Pair<>(currentModuleCode, modulePQ.timeSlotDescriptionMap.get(
                                    new Pair<>(currentModuleCode, currentTimeSlotID))), vacancy-1);
                    if (!check()){
                        requiredLabel.setVisible(false);
                    } else {
                        requiredLabel.setVisible(true);
                    }
                    signUpButton.setDisable(true);
                }
            }
        });

        logoutButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    reset();
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
    private static void write() throws IOException {
        FileWriter outputStream = new FileWriter("ModulePriorityQueue.txt");
        FileWriter outputStream1 = new FileWriter("ModulesAndTimeSlots.txt");
        for (String moduleCode : moduleSet) {
            for (int id:modulePQ.getTimeSlotIDs(moduleCode)){
                if (!modulePQ.PQIsEmpty(modulePQ.getTimeSlotPQ(moduleCode,id))){
                    outputStream.write(moduleCode + "," + id + "," + modulePQ.PQLength(modulePQ.getTimeSlotPQ(moduleCode,id))+"\n");
                }
                ArrayList<Pair<String,Integer>> arrayList=new ArrayList<>();
                while (!modulePQ.PQIsEmpty(modulePQ.getTimeSlotPQ(moduleCode,id))){
                    Pair<String, Integer> pair=modulePQ.dequeueFromTimeSlot(moduleCode,id);
                    outputStream.write(pair.getKey() + "," + pair.getValue() +"\n");
                    arrayList.add(pair);
                }
                for (Pair<String,Integer> pair: arrayList){
                    modulePQ.enqueueToTimeSlot(moduleCode,id,pair.getKey(),pair.getValue());
                }
                String timeSlotDescription = modulePQ.getTimeSlot(moduleCode, id);
                outputStream1.write(moduleCode+","+id+","+timeSlotDescription+","
                        +timeVacancy.get(new Pair<>(moduleCode, timeSlotDescription)) + ','
                        +timeTotal.get(new Pair<>(moduleCode, timeSlotDescription)) + "\n");
            }
        }
        outputStream.close();
        outputStream1.close();
    }
    public static void reset(){
        modulePQ = new ModulePriorityQueue<String, Integer>();
        moduleSet = new TreeSet<String>();
        moduleDetails = new HashMap<>();
        moduleDescriptions = new HashMap<>();
        studentsRequiredModules = new TreeSet<String>();
        registeredModules = new HashMap<>();
        timeVacancy= new HashMap<>();
        timeTotal= new HashMap<>();
        availablePreferences = new ArrayList<>();
    }


    public static boolean check(){
        boolean missed=false;
        for (String module: studentsRequiredModules) {
            if (!registeredModules.containsKey(module)){
                missed=true;
                break;
            }
        }
        return missed;
    }


}
