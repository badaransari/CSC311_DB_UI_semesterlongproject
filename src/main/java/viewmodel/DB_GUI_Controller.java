package viewmodel;

import com.sun.javafx.menu.MenuItemBase;
import dao.DbConnectivityClass;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Person;
import service.MyLogger;

import java.io.*;
import java.net.URL;
import java.time.LocalDate;
import java.util.Optional;
import java.util.ResourceBundle;

public class DB_GUI_Controller implements Initializable {

    public Label statusLabel;
    @FXML
    TextField first_name, last_name, department, major, email, imageURL;
    @FXML
    ImageView img_view;
    @FXML
    MenuBar menuBar;
    @FXML
    private TableView<Person> tv;
    @FXML
    private TableColumn<Person, Integer> tv_id;
    @FXML
    private TableColumn<Person, String> tv_fn, tv_ln, tv_department, tv_major, tv_email;
    private final DbConnectivityClass cnUtil = new DbConnectivityClass();
    private final ObservableList<Person> data = cnUtil.getData();
    private MenuItemBase editRecord;
    private MenuItemBase deleteButton;
    private MenuItemBase addButton;
    private MenuItemBase editMenuItem;
    private MenuItemBase deleteMenuItem;
    @FXML
    private ComboBox<Major> majorComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            tv_id.setCellValueFactory(new PropertyValueFactory<>("id"));
            tv_fn.setCellValueFactory(new PropertyValueFactory<>("firstName"));
            tv_ln.setCellValueFactory(new PropertyValueFactory<>("lastName"));
            tv_department.setCellValueFactory(new PropertyValueFactory<>("department"));
            tv_major.setCellValueFactory(new PropertyValueFactory<>("major"));
            tv_email.setCellValueFactory(new PropertyValueFactory<>("email"));
            tv.setItems(data);
            // Disable "Edit" button initially
            editRecord.setDisable(true);

            // Add a listener to enable/disable "Edit" button based on selection
            tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Enable "Edit" button when a record is selected
                    editRecord.setDisable(false);
                } else {
                    // Disable "Edit" button when no record is selected
                    editRecord.setDisable(true);
                }
            });
// Disable "Edit" and "Delete" menu items initially
            editMenuItem.setDisable(true);
            deleteMenuItem.setDisable(true);

            // Add a listener to enable/disable menu items based on selection
            tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Enable "Edit" and "Delete" menu items when a record is selected
                    editMenuItem.setDisable(false);
                    deleteMenuItem.setDisable(false);
                } else {
                    // Disable "Edit" and "Delete" menu items when no record is selected
                    editMenuItem.setDisable(true);
                    deleteMenuItem.setDisable(true);
                }
            });

// Disable "Add" button initially
            addButton.setDisable(true);

            // Add listeners to form fields for validation
            first_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            last_name.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            department.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            major.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            email.textProperty().addListener((observable, oldValue, newValue) -> validateForm());
            imageURL.textProperty().addListener((observable, oldValue, newValue) -> validateForm());

// Disable "Delete" button initially
            deleteButton.setDisable(true);

            // Add a listener to enable/disable "Delete" button based on selection
            tv.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    // Enable "Delete" button when a record is selected
                    deleteButton.setDisable(false);
                } else {
                    // Disable "Delete" button when no record is selected
                    deleteButton.setDisable(true);
                }
            });

            // Initialize the Major with enum values
            majorComboBox.setItems(FXCollections.observableArrayList(Major.values()));
            majorComboBox.getSelectionModel().selectFirst();


            // Clear status label
            statusLabel.setText("");

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @FXML
    protected void addNewRecord() {

            Person p = new Person(first_name.getText(), last_name.getText(), department.getText(),
                    major.getText(), email.getText(), imageURL.getText());
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();

    }

    @FXML
    protected void clearForm() {
        first_name.setText("");
        last_name.setText("");
        department.setText("");
        major.setText("");
        email.setText("");
        imageURL.setText("");
    }

    @FXML
    protected void logOut(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").getFile());
            Stage window = (Stage) menuBar.getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void closeApplication() {
        System.exit(0);
    }

    @FXML
    protected void displayAbout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/about.fxml"));
            Stage stage = new Stage();
            Scene scene = new Scene(root, 600, 500);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    protected void editRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        Person p2 = new Person(index + 1, first_name.getText(), last_name.getText(), department.getText(),
                major.getText(), email.getText(),  imageURL.getText());
        cnUtil.editUser(p.getId(), p2);
        data.remove(p);
        data.add(index, p2);
        tv.getSelectionModel().select(index);
         // Set status message
        setStatusMessage("Record updated successfully.");

    }

    @FXML
    protected void deleteRecord() {
        Person p = tv.getSelectionModel().getSelectedItem();
        int index = data.indexOf(p);
        cnUtil.deleteRecord(p);
        data.remove(index);
        tv.getSelectionModel().select(index);
    }

    @FXML
    protected void showImage() {
        File file = (new FileChooser()).showOpenDialog(img_view.getScene().getWindow());
        if (file != null) {
            img_view.setImage(new Image(file.toURI().toString()));
        }
    }

   // @FXML
    //protected void addRecord() {
     //   showSomeone();
    //}

    @FXML
    protected void addRecord() {
        if (validateForm()) {
            String firstName = first_name.getText().trim();
            String lastName = last_name.getText().trim();
            String departmentValue = department.getText().trim();
            Major majorValue = majorComboBox.getValue(); // Use the selected value from the ComboBox
            String emailValue = email.getText().trim();
            String imageURLValue = imageURL.getText().trim();

            // Perform field-level validation
            if (firstName.isEmpty() || lastName.isEmpty() || departmentValue.isEmpty() ||
                    majorValue == null || emailValue.isEmpty() || imageURLValue.isEmpty()) {
                // Show an alert or handle validation error
                showAlert("All fields must be filled.");
                return;
            }

            // If all validations pass, proceed to add the record
            Person p = new Person(firstName, lastName, departmentValue, major, emailValue, imageURLValue);
            cnUtil.insertUser(p);
            cnUtil.retrieveId(p);
            p.setId(cnUtil.retrieveId(p));
            data.add(p);
            clearForm();
            // Set status message
            setStatusMessage("Record added successfully.");

        }
    }

    private void setStatusMessage(String s) {
        statusLabel.setText(s);

        // You can clear the status message after a certain duration if needed
        PauseTransition visiblePause = new PauseTransition(Duration.seconds(5));
        visiblePause.setOnFinished(event -> statusLabel.setText(""));
        visiblePause.play();
    }


    private boolean validateForm() {
        return true;
    }



    private void showAlert(String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(s);
        alert.showAndWait();
    }






    @FXML
    protected void selectedItemTV(MouseEvent mouseEvent) {
        Person p = tv.getSelectionModel().getSelectedItem();
        first_name.setText(p.getFirstName());
        last_name.setText(p.getLastName());
        department.setText(p.getDepartment());
        major.setText(p.getMajor());
        email.setText(p.getEmail());
        imageURL.setText(p.getImageURL());
    }

    public void lightTheme(ActionEvent actionEvent) {
        try {
            Scene scene = menuBar.getScene();
            Stage stage = (Stage) scene.getWindow();
            stage.getScene().getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            stage.setScene(scene);
            stage.show();
            System.out.println("light " + scene.getStylesheets());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void darkTheme(ActionEvent actionEvent) {
        try {
            Stage stage = (Stage) menuBar.getScene().getWindow();
            Scene scene = stage.getScene();
            scene.getStylesheets().clear();
            scene.getStylesheets().add(getClass().getResource("/css/darkTheme.css").toExternalForm());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSomeone() {
        Dialog<Results> dialog = new Dialog<>();
        dialog.setTitle("New User");
        dialog.setHeaderText("Please specify…");
        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        TextField textField1 = new TextField("Name");
        TextField textField2 = new TextField("Last Name");
        TextField textField3 = new TextField("Email ");
        ObservableList<Major> options =
                FXCollections.observableArrayList(Major.values());
        ComboBox<Major> comboBox = new ComboBox<>(options);
        comboBox.getSelectionModel().selectFirst();
        dialogPane.setContent(new VBox(8, textField1, textField2,textField3, comboBox));
        Platform.runLater(textField1::requestFocus);
        dialog.setResultConverter((ButtonType button) -> {
            if (button == ButtonType.OK) {
                return new Results(textField1.getText(),
                        textField2.getText(), comboBox.getValue());
            }
            return null;
        });
        Optional<Results> optionalResult = dialog.showAndWait();
        optionalResult.ifPresent((Results results) -> {
            MyLogger.makeLog(
                    results.fname + " " + results.lname + " " + results.major);
        });
    }


    @FXML
    protected void importCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Import CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showOpenDialog(tv.getScene().getWindow());

        if (selectedFile != null) {
            // Implement logic to read data from the CSV file and update your data model
            // For example, you can use a CSV parsing library or implement a simple CSV reader.
            // Here, I assume a simple CSV structure with comma-separated values.

            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(",");
                    // Assuming your Person class has appropriate constructors
                    Person person = new Person(parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                    data.add(person);
                }
                setStatusMessage("CSV file imported successfully.");
            } catch (IOException e) {
                setStatusMessage("Error importing CSV file: " + e.getMessage());
            }
        }
    }

    @FXML
    protected void exportCSV() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Export CSV File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV Files", "*.csv"));
        File selectedFile = fileChooser.showSaveDialog(tv.getScene().getWindow());

        if (selectedFile != null) {
            // Implement logic to write data to the CSV file
            // For example, you can use a CSV writing library or manually create a CSV string.

            try (PrintWriter writer = new PrintWriter(selectedFile)) {
                for (Person person : data) {
                    // Assuming your Person class has appropriate getters
                    String csvLine = String.join(",", person.getFirstName(), person.getLastName(), person.getDepartment(),
                            person.getMajor(), person.getEmail(), person.getImageURL());
                    writer.println(csvLine);
                }
                setStatusMessage("CSV file exported successfully.");
            } catch (IOException e) {
                setStatusMessage("Error exporting CSV file: " + e.getMessage());
            }
        }
    }

    private static enum Major {Business, CSC, CPIS}


    private static class Results {

        String fname;
        String lname;
        Major major;

        public Results(String name, String date, Major venue) {
            this.fname = name;
            this.lname = date;
            this.major = venue;
        }
    }

}