import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

/**
 * Controller Class - Registration Controller
 * Handles new customer registration
 */
public class RegisterController {
    private Stage stage;
    private BankSystemWithDAO bankSystem;
    
    public RegisterController(Stage stage, BankSystemWithDAO bankSystem) {
        this.stage = stage;
        this.bankSystem = bankSystem;
    }
    
    public void showRegisterView() {
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(30));
        layout.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Register New Customer");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        TextField firstNameField = new TextField();
        firstNameField.setPromptText("First Name");
        
        TextField lastNameField = new TextField();
        lastNameField.setPromptText("Last Name");
        
        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        
        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone");
        
        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        
        Label messageLabel = new Label();
        
        Button registerButton = new Button("Register");
        registerButton.setPrefWidth(200);
        registerButton.setOnAction(e -> {
            String result = handleRegister(
                firstNameField.getText(),
                lastNameField.getText(),
                emailField.getText(),
                phoneField.getText(),
                addressField.getText()
            );
            
            if (result.startsWith("Success")) {
                messageLabel.setStyle("-fx-text-fill: green;");
                messageLabel.setText(result);
            } else {
                messageLabel.setStyle("-fx-text-fill: red;");
                messageLabel.setText(result);
            }
        });
        
        Button backButton = new Button("Back to Login");
        backButton.setOnAction(e -> {
            LoginController loginController = new LoginController(stage, bankSystem);
            loginController.showLoginView();
        });
        
        layout.getChildren().addAll(
            titleLabel, firstNameField, lastNameField, emailField,
            phoneField, addressField, registerButton, messageLabel, backButton
        );
        
        Scene scene = new Scene(layout, 400, 500);
        stage.setScene(scene);
        stage.setTitle("Register");
        stage.show();
    }
    
    private String handleRegister(String firstName, String lastName, 
                                  String email, String phone, String address) {
        // Validation
        if (firstName == null || firstName.trim().isEmpty()) {
            return "First name is required";
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            return "Last name is required";
        }
        if (email == null || email.trim().isEmpty()) {
            return "Email is required";
        }
        
        // Delegate to business logic
        CustomerClass customer = bankSystem.createCustomer(
            firstName, lastName, email, phone, address
        );
        
        if (customer != null) {
            return "Success! Customer created: " + customer.getCustomerId();
        } else {
            return "Registration failed - email may already exist";
        }
    }
}