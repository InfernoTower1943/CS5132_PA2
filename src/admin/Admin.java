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
import java.util.*;

import javafx.util.Pair;
import main.Main;
import main.ModulePriorityQueue;
import student.Student;

public class Admin extends Application{
    private Stage globalStage;

    public static main.ModulePriorityQueue<String, Integer> modulePQ = new ModulePriorityQueue<String, Integer>();
    public static SortedSet<String> moduleSet = new TreeSet<String>();
    public static Map<String, String> moduleTitles = new HashMap<>();
    public static Map<String, String> moduleDescriptions = new HashMap<>();
    public static Map<String, Integer> modulePreferencePenalties = new HashMap<>();

    public static Map<Pair<String, String>, Integer> timeVacancy= new HashMap<>();
    public static Map<Pair<String, String>, Integer> timeTotal= new HashMap<>();

    public static SortedSet<String> studentSet = new TreeSet<String>();
    public static Map<String, SortedSet<String>> studentsRequiredModules = new HashMap<>();
    public static String selectedModule = "";
    public static String selectedStudent = "";
    public static String selectedTimeSlot = "";

    public static void main(String[] args) {
        Application.launch(args);
    }


    @FXML
    SplitPane mainSplitPane;
    @FXML
    ScrollPane firstPane;
    @FXML
    VBox moduleDetailsPane;
    @FXML
    VBox studentDetailsPane;
    @FXML
    TabPane adminModuleStudentTabPane;

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
    TextField adminPenaltyTextBox;
    @FXML
    TextArea moduleDescriptionTextBox;

    @FXML
    Label studentDetailsLabel;

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
    Button adminViewPQButton;
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

        Scanner scanner = new Scanner(new File(System.getProperty("user.dir")+"/"+"ModulesAndTimeSlots.txt"));
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
        scanner = new Scanner(new File(System.getProperty("user.dir")+"/"+"StudentLoginDetails.txt"));
        scanner.useDelimiter("\n");
        while(scanner.hasNext()){
            String line = scanner.next();
            String[] args = line.split(",");
            studentSet.add(args[0]);
        }
        scanner.close();

        scanner = new Scanner(new File(System.getProperty("user.dir")+"/"+"ModuleDetails.txt"));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] args = line.split(",", 4);
            moduleTitles.put(args[0], args[1]);
            modulePreferencePenalties.put(args[0], Integer.parseInt(args[2]));
            moduleDescriptions.put(args[0], args[3]);
        }
        scanner.close();

        scanner = new Scanner(new File(System.getProperty("user.dir")+"/"+"StudentRequiredModules.txt"));
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

        scanner = new Scanner(new File(System.getProperty("user.dir")+"/"+"ModulePriorityQueue.txt"));
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
            }
        }
        scanner.close();

        mainSplitPane = (SplitPane) loader.getNamespace().get("mainSplitPane");

        firstPane = (ScrollPane) loader.getNamespace().get("firstPane");
        moduleDetailsPane = (VBox) loader.getNamespace().get("moduleDetailsVBox");
        studentDetailsPane = (VBox) loader.getNamespace().get("studentDetailsVBox");


        adminAllModulesListView = (ListView) loader.getNamespace().get("adminAllModulesListView");
        adminAllModulesListView.getItems().addAll(moduleSet);
        adminAvailableTimeSlotsComboBox = (ComboBox) loader.getNamespace().get("adminAvailableTimeSlotsComboBox");

        adminAllStudentsListView = (ListView) loader.getNamespace().get("adminAllStudentsListView");
        adminAllStudentsListView.getItems().addAll(studentSet);
        adminRequiredModulesListView = (ListView) loader.getNamespace().get("adminRequiredModulesListView");
        adminModuleStudentTabPane = (TabPane) loader.getNamespace().get("adminModuleStudentTabPane");

        adminModuleTitleTextBox = (TextField) loader.getNamespace().get("adminModuleTitleTextBox");
        adminModuleCodeTextBox = (TextField) loader.getNamespace().get("adminModuleCodeTextBox");
        studentDetailsLabel = (Label) loader.getNamespace().get("studentDetailsLabel");

        // TODO: make total spots text box numeric only
        adminTotalSpotsTextBox = (TextField) loader.getNamespace().get("adminTotalSpotsTextBox");
        adminPenaltyTextBox = (TextField) loader.getNamespace().get("adminPenaltyTextBox");
        moduleDescriptionTextBox = (TextArea) loader.getNamespace().get("moduleDescriptionTextBox");
        timeSlotEditButton = (Button) loader.getNamespace().get("timeSlotEditButton");
        timeSlotSaveButton = (Button) loader.getNamespace().get("timeSlotSaveButton");
        descriptionEditButton = (Button) loader.getNamespace().get("descriptionEditButton");
        descriptionSaveButton = (Button) loader.getNamespace().get("descriptionSaveButton");
        adminViewPQButton = (Button) loader.getNamespace().get("adminViewPQButton");
        requiredModulesEditButton = (Button) loader.getNamespace().get("requiredModulesEditButton");
        requiredModulesSaveButton = (Button) loader.getNamespace().get("requiredModulesSaveButton");
        logoutButton = (Button) loader.getNamespace().get("logoutButton");

        adminAllModulesListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {

                        // make student details VBox invisible and expand the module details VBox pane somehow
                        moduleDetailsPane.setVisible(true);
                        studentDetailsPane.setVisible(false);
                        mainSplitPane.setDividerPositions(0.25, 1);

                        // Update choice box
                        selectedModule = (String) newValue;
                        adminAvailableTimeSlotsComboBox.getItems().clear();
                        for (Integer timeSlotID : modulePQ.getTimeSlotIDs(selectedModule)){
                            adminAvailableTimeSlotsComboBox.getItems().add(modulePQ.getTimeSlot(selectedModule, timeSlotID));
                        }
                        // set module moduleTitle label
                        adminModuleTitleTextBox.setText(moduleTitles.get(selectedModule));
                        // set module moduleCode label
                        adminModuleCodeTextBox.setText(selectedModule);
                        // set module timeSlotDescription
                        moduleDescriptionTextBox.setText(moduleDescriptions.get(selectedModule));

                        adminTotalSpotsTextBox.setText("");
                        adminPenaltyTextBox.setText(Integer.toString(modulePreferencePenalties.get(selectedModule)));
                    }
                });

        adminAvailableTimeSlotsComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {

                        selectedTimeSlot = (String) newValue;
                        adminTotalSpotsTextBox.setText(Integer.toString(timeTotal.get(
                                new Pair<>(selectedModule, selectedTimeSlot))));

                        timeSlotEditButton.setDisable(false);

                    }else{
                        timeSlotEditButton.setDisable(true);
                        timeSlotSaveButton.setDisable(true);
                    }
                });

        adminAllStudentsListView.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    if (newValue != null) {
                        // make modules details VBox invisible and expand the student details VBox pane somehow
                        moduleDetailsPane.setVisible(false);
                        studentDetailsPane.setVisible(true);
                        mainSplitPane.setDividerPositions(0.25, 0.25);

                        String studentID = (String) newValue;
                        selectedStudent = studentID;
                        studentDetailsLabel.setText("Student Details - "+studentID);
                        adminRequiredModulesListView.getItems().clear();

                        SortedSet<String> required = studentsRequiredModules.get(studentID);
                        for (String moduleCode : moduleSet){
                            CheckBox cb = new CheckBox(moduleCode);
                            if (required.contains(moduleCode)){
                                cb.setSelected(true);
                            }
                            adminRequiredModulesListView.getItems().add(cb);
                        }
                    }
                });

        timeSlotEditButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminModuleStudentTabPane.setDisable(true);
                adminModuleTitleTextBox.setEditable(true);
                adminModuleCodeTextBox.setEditable(true);
                adminTotalSpotsTextBox.setEditable(true);
                adminPenaltyTextBox.setEditable(true);
                timeSlotEditButton.setDisable(true);
                timeSlotSaveButton.setDisable(false);
            }
        });

        timeSlotSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminModuleStudentTabPane.setDisable(false);
                adminModuleTitleTextBox.setEditable(false);
                adminModuleCodeTextBox.setEditable(false);
                adminTotalSpotsTextBox.setEditable(false);
                adminPenaltyTextBox.setEditable(false);
                timeSlotEditButton.setDisable(false);
                timeSlotSaveButton.setDisable(true);
                moduleTitles.remove(selectedModule);

                String moduleTitle = adminModuleTitleTextBox.getText();
                String moduleCode = adminModuleCodeTextBox.getText();
                String selectedTimeSlot = adminAvailableTimeSlotsComboBox.getValue().toString();
                Integer totalSpots = Integer.valueOf(-1);
                Integer penalty = Integer.valueOf(-1);
                try {
                    totalSpots = Integer.parseInt(adminTotalSpotsTextBox.getText());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }
                try {
                    penalty = Integer.parseInt(adminPenaltyTextBox.getText());
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                    System.out.println(e);
                }

                if (totalSpots != -1){
                    timeTotal.remove(new Pair<>(moduleCode, selectedTimeSlot));
                    timeTotal.put(new Pair<>(moduleCode, selectedTimeSlot), totalSpots);
                }
                if (penalty != -1){
                    modulePreferencePenalties.put(moduleCode, penalty);
                }

                moduleTitles.put(moduleCode,moduleTitle);
                moduleSet.remove(selectedModule);
                moduleSet.add(moduleCode);

                adminAllModulesListView.getItems().clear();
                adminAllModulesListView.getItems().addAll(moduleSet);

                ArrayList<Integer> selectedModuleAllTimeSlots =new ArrayList<>(modulePQ.getTimeSlotIDs(selectedModule));
                for (Integer timeSlotID : selectedModuleAllTimeSlots){
                    String timeSlotDescription=modulePQ.getTimeSlot(selectedModule,timeSlotID);
                    modulePQ.deleteTimeSlot(selectedModule,timeSlotID);
                    modulePQ.addTimeSlot(moduleCode,timeSlotID,timeSlotDescription);
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
                adminModuleStudentTabPane.setDisable(true);
                moduleDescriptionTextBox.setEditable(true);
                descriptionEditButton.setDisable(true);
                descriptionSaveButton.setDisable(false);
            }
        });

        descriptionSaveButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                adminModuleStudentTabPane.setDisable(false);
                moduleDescriptionTextBox.setEditable(false);
                descriptionEditButton.setDisable(false);
                descriptionSaveButton.setDisable(true);
                String timeSlotDescription=moduleDescriptionTextBox.getText();
                moduleDescriptions.put(selectedModule,timeSlotDescription);
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

        adminViewPQButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                boolean flag=true;
                try {
                    PQView.moduleTitle = adminModuleTitleTextBox.getText();
                    PQView.moduleCode = adminModuleCodeTextBox.getText();
                    PQView.timeSlot = ""+adminAvailableTimeSlotsComboBox.getValue();
                    ArrayList<Integer> currentModuleAllTimeSlots = new ArrayList<>(modulePQ.getTimeSlotIDs(selectedModule));
                    for (Integer timeSlotID : currentModuleAllTimeSlots) {
                        if (modulePQ.timeSlotDescriptionMap.get(new Pair(PQView.moduleCode, timeSlotID)).equals(PQView.timeSlot)){
                            try {
                                PQView.studentID = (String) modulePQ.getTimeSlotPQ(selectedModule, timeSlotID).top().getItem();
                            }catch(Exception e){
                                Alert a = new Alert(Alert.AlertType.WARNING);
                                a.setHeaderText("Priority Queue is Empty!");
                                flag=false;
                                a.show();
                            }
                        }
                    }
                    PQView.pref = "TODO";

                    if (flag) new PQView().start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
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

    public static void removePQTop(){
        ArrayList<Integer> currentModuleAllTimeSlots = new ArrayList<>(modulePQ.getTimeSlotIDs(selectedModule));
        for (Integer timeSlotID : currentModuleAllTimeSlots) {
            if (modulePQ.timeSlotDescriptionMap.get(new Pair(PQView.moduleCode, timeSlotID)).equals(PQView.timeSlot)){
                try {
                    String student = modulePQ.getTimeSlotPQ(selectedModule, timeSlotID).dequeue();
                    Alert a = new Alert(Alert.AlertType.CONFIRMATION);
                    a.setHeaderText("Dequeued student " + student + "!");
                    a.show();
                    write();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    private static void write() throws IOException {
        FileWriter outputStream = new FileWriter(System.getProperty("user.dir")+"/"+"ModuleDetails.txt");
        FileWriter outputStream1 = new FileWriter(System.getProperty("user.dir")+"/"+"ModulesAndTimeSlots.txt");
        FileWriter outputStream2 = new FileWriter(System.getProperty("user.dir")+"/"+"ModulePriorityQueue.txt");
        for (String moduleCode : moduleSet) {
            outputStream.write(moduleCode + "," + moduleTitles.get(moduleCode) + "," + (modulePreferencePenalties.get(moduleCode)) + "," + moduleDescriptions.get(moduleCode)+"\n");
            ArrayList<Integer> currentModuleAllTimeSlots = new ArrayList<>(modulePQ.getTimeSlotIDs(moduleCode));
            for (Integer timeSlotID : currentModuleAllTimeSlots) {
                    if (!modulePQ.PQIsEmpty(modulePQ.getTimeSlotPQ(moduleCode,timeSlotID))){
                        outputStream2.write(moduleCode + "," + timeSlotID + "," + modulePQ.PQLength(modulePQ.getTimeSlotPQ(moduleCode,timeSlotID))+"\n");
                    }
                    ArrayList<Pair<String,Integer>> arrayList=new ArrayList<>();
                    while (!modulePQ.PQIsEmpty(modulePQ.getTimeSlotPQ(moduleCode,timeSlotID))){
                        Pair<String, Integer> pair=modulePQ.dequeueFromTimeSlot(moduleCode,timeSlotID);
                        outputStream2.write(pair.getKey() + "," + pair.getValue() +"\n");
                        arrayList.add(pair);
                    }
                    for (Pair<String,Integer> pair: arrayList){
                        modulePQ.enqueueToTimeSlot(moduleCode,timeSlotID,pair.getKey(),pair.getValue());
                    }
                String timeSlotDescription = modulePQ.getTimeSlot(moduleCode, timeSlotID);
                outputStream1.write(moduleCode+","+timeSlotID+","+timeSlotDescription+","
                        +timeVacancy.get(new Pair<>(moduleCode, timeSlotDescription)) + ','
                        +timeTotal.get(new Pair<>(moduleCode, timeSlotDescription)) + "\n");
            }
        }
        outputStream.close();
        outputStream1.close();
        outputStream2.close();
    }
}
