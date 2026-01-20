import javafx.stage.Stage;

/**
 * Controller Class - Login Controller
 * Mediates between LoginView and business logic
 * NO BUSINESS LOGIC - only coordination
 */
public class LoginController {
    private Stage stage;
    private BankSystemWithDAO bankSystem;
    private LoginView loginView;
    
    public LoginController(Stage stage, BankSystemWithDAO bankSystem) {
        this.stage = stage;
        this.bankSystem = bankSystem;
        this.loginView = new LoginView(stage, this);
    }
    
    public void showLoginView() {
        stage.setScene(loginView.createScene());
        stage.setTitle("Banking System - Login");
        stage.show();
    }
    
    /**
     * Handle login request - coordinates authentication
     */
    public void handleLogin(String email, String password) {
        // Input validation only
        if (email == null || email.trim().isEmpty()) {
            loginView.showError("Email is required");
            return;
        }
        
        if (password == null || password.isEmpty()) {
            loginView.showError("Password is required");
            return;
        }
        
        // Call business logic layer
        CustomerClass customer = bankSystem.findCustomerByEmail(email);
        
        if (customer != null) {
            // In real app, verify password hash
            // For demo, we accept any password if customer exists
            loginView.showSuccess("Login successful!");
            
            // Navigate to account view
            showAccountView(customer);
        } else {
            loginView.showError("Invalid email or password");
        }
    }
    
    /**
     * Navigate to account management view
     */
    private void showAccountView(CustomerClass customer) {
        AccountController accountController = new AccountController(stage, bankSystem, customer);
        accountController.showAccountView();
    }
    
    /**
     * Show registration view
     */
    public void showRegisterView() {
        RegisterController registerController = new RegisterController(stage, bankSystem);
        registerController.showRegisterView();
    }
}