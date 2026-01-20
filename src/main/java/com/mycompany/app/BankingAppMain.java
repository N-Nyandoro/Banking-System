import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Application Class - Entry Point
 * Integrates all modules: GUI (View), Controller, Model, and Database (DAO)
 */
public class BankingApplication extends Application {
    
    private BankSystemWithDAO bankSystem;
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Initialize database and create sample data
            initializeSystem();
            
            // Create and show login view
            LoginController loginController = new LoginController(primaryStage, bankSystem);
            loginController.showLoginView();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Application startup failed: " + e.getMessage());
        }
    }
    
    /**
     * Initialize the banking system with database and sample data
     */
    private void initializeSystem() {
        System.out.println("=== Initializing Banking System ===");
        
        // Step 1: Initialize database
        DatabaseConnection.initializeDatabase();
        System.out.println("✓ Database initialized");
        
        // Step 2: Create bank system
        bankSystem = new BankSystemWithDAO("FirstBank Digital");
        System.out.println("✓ Bank system created: " + bankSystem.getBankName());
        
        // Step 3: Create sample customers and accounts
        createSampleData();
        System.out.println("✓ Sample data created");
        
        System.out.println("=== System Ready ===\n");
    }
    
    /**
     * Create sample data for testing
     */
    private void createSampleData() {
        // Create customers
        CustomerClass customer1 = bankSystem.createCustomer(
            "John", "Doe",
            "john.doe@email.com",
            "555-0101",
            "123 Main Street, Cityville"
        );
        
        CustomerClass customer2 = bankSystem.createCustomer(
            "Jane", "Smith",
            "jane.smith@email.com",
            "555-0102",
            "456 Oak Avenue, Townsburg"
        );
        
        if (customer1 != null) {
            // Create accounts for customer1
            ChequeAccountClass cheque1 = bankSystem.createChequeAccount(customer1.getCustomerId());
            SavingsAccountClass savings1 = bankSystem.createSavingsAccount(
                customer1.getCustomerId(), 1000.0
            );
            InvestmentAccountClass invest1 = bankSystem.createInvestmentAccount(
                customer1.getCustomerId(), "Fixed Deposit", 5000.0
            );
            
            // Add some transactions
            if (cheque1 != null) {
                bankSystem.deposit(cheque1.getAccountNumber(), 2000.0);
                bankSystem.withdraw(cheque1.getAccountNumber(), 500.0);
            }
        }
        
        if (customer2 != null) {
            // Create accounts for customer2
            ChequeAccountClass cheque2 = bankSystem.createChequeAccount(customer2.getCustomerId());
            SavingsAccountClass savings2 = bankSystem.createSavingsAccount(
                customer2.getCustomerId(), 500.0
            );
            
            if (cheque2 != null) {
                bankSystem.deposit(cheque2.getAccountNumber(), 1500.0);
            }
        }
        
        System.out.println("Sample users created:");
        System.out.println("  - john.doe@email.com (password: any)");
        System.out.println("  - jane.smith@email.com (password: any)");
    }
    
    @Override
    public void stop() {
        System.out.println("\n=== Shutting Down ===");
        DatabaseConnection.closeConnection();
        System.out.println("✓ Database connection closed");
        System.out.println("=== Goodbye ===");
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}