package viewmodel;

import com.sun.javafx.charts.Legend;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.util.HashSet;
import java.util.Set;

public class SignUpController {
    private static final Set<String> registeredUsernames = new HashSet<>(); // Simulating a set of registered usernames

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private TextField emailField;
    private Object userService;


    // UserService is a service class responsible for user-related operations
   
    public void createNewAccount(ActionEvent actionEvent) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Info for the user. Message goes here");
        alert.showAndWait();
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/view/login.fxml"));
            Scene scene = new Scene(root, 900, 600);
            scene.getStylesheets().add(getClass().getResource("/css/lightTheme.css").toExternalForm());
            Stage window = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            window.setScene(scene);
            window.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




        @FXML
        public void signUp(ActionEvent actionEvent) {
            String username = usernameField.getText();
            String password = passwordField.getText();
            String email = emailField.getText();

            // Validate input (you may add more comprehensive validation)
            if (username.isEmpty() || password.isEmpty() || email.isEmpty()) {
                showAlert("Please fill in all fields.");
                return;
            }

            // Check if the username is already taken
            if (registeredUsernames.contains(username)) {
                showAlert("Username already taken. Please choose another.");
                return;
            }

            // Assuming successful signup if the username is not taken
            registeredUsernames.add(username);

            showAlert("Account created successfully!");
            // You may navigate to another page, close the signup window, etc.
        }

        private void showAlert(String message) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setContentText(message);
            alert.showAndWait();
        }
    }









