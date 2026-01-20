import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

/**
 * Boundary Class - Login View
 * Handles user login interface
 */
public class LoginView {
    private Stage stage;
    private LoginController controller;
    
    private TextField emailField;
    private PasswordField passwordField;
    private Label messageLabel;
    
    public LoginView(Stage stage, LoginController controller) {
        this.stage = stage;
        this.controller = controller;
    }
    
    public Scene createScene() {
        // Main container
        VBox mainLayout = new VBox(20);
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.setPadding(new Insets(40));
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Title
        Label titleLabel = new Label("Banking System");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 32));
        titleLabel.setStyle("-fx-text-fill: #2c3e50;");
        
        Label subtitleLabel = new Label("Please login to continue");
        subtitleLabel.setFont(Font.font("Arial", 14));
        subtitleLabel.setStyle("-fx-text-fill: #7f8c8d;");
        
        // Login form
        VBox formBox = new VBox(15);
        formBox.setMaxWidth(400);
        formBox.setPadding(new Insets(30));
        formBox.setStyle("-fx-background-color: white; -fx-background-radius: 10;");
        
        // Email field
        Label emailLabel = new Label("Email:");
        emailLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        emailField = new TextField();
        emailField.setPromptText("Enter your email");
        emailField.setPrefHeight(40);
        emailField.setStyle("-fx-font-size: 14px;");
        
        // Password field
        Label passwordLabel = new Label("Password:");
        passwordLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.setPrefHeight(40);
        passwordField.setStyle("-fx-font-size: 14px;");
        
        // Login button
        Button loginButton = new Button("Login");
        loginButton.setPrefWidth(340);
        loginButton.setPrefHeight(45);
        loginButton.setStyle(
            "-fx-background-color: #3498db; " +
            "-fx-text-fill: white; " +
            "-fx-font-size: 16px; " +
            "-fx-font-weight: bold; " +
            "-fx-background-radius: 5;"
        );
        loginButton.setOnAction(e -> handleLogin());
        
        // Message label
        messageLabel = new Label();
        messageLabel.setFont(Font.font("Arial", 12));
        messageLabel.setWrapText(true);
        messageLabel.setMaxWidth(340);
        messageLabel.setAlignment(Pos.CENTER);
        
        // Demo credentials info
        Label demoLabel = new Label("Demo: john.doe@email.com / password123");
        demoLabel.setFont(Font.font("Arial", 11));
        demoLabel.setStyle("-fx-text-fill: #95a5a6;");
        
        // Register link
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register here");
        registerLink.setOnAction(e -> controller.showRegisterView());
        
        formBox.getChildren().addAll(
            emailLabel, emailField,
            passwordLabel, passwordField,
            loginButton, messageLabel, demoLabel, registerLink
        );
        
        mainLayout.getChildren().addAll(titleLabel, subtitleLabel, formBox);
        
        // Handle Enter key
        passwordField.setOnAction(e -> handleLogin());
        
        return new Scene(mainLayout, 600, 550);
    }
    
    private void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        
        if (email.isEmpty() || password.isEmpty()) {
            showError("Please enter both email and password");
            return;
        }
        
        controller.handleLogin(email, password);
    }
    
    public void showError(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #e74c3c;");
    }
    
    public void showSuccess(String message) {
        messageLabel.setText(message);
        messageLabel.setStyle("-fx-text-fill: #27ae60;");
    }
    
    public void clearFields() {
        emailField.clear();
        passwordField.clear();
        messageLabel.setText("");
    }
}