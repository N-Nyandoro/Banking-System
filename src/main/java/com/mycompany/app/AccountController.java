import javafx.stage.Stage;
import java.util.List;

/**
 * Controller Class - Account Controller
 * Mediates between AccountView and business logic/database
 * NO BUSINESS LOGIC - only coordination and validation
 */
public class AccountController {
    private Stage stage;
    private BankSystemWithDAO bankSystem;
    private CustomerClass currentCustomer;
    private AccountView accountView;
    
    public AccountController(Stage stage, BankSystemWithDAO bankSystem, CustomerClass customer) {
        this.stage = stage;
        this.bankSystem = bankSystem;
        this.currentCustomer = customer;
        this.accountView = new AccountView(stage, this, customer);
    }
    
    public void showAccountView() {
        stage.setScene(accountView.createScene());
        stage.setTitle("Banking System - Account Management");
        accountView.refreshAccountList();
        stage.show();
    }
    
    /**
     * Handle deposit request - validation only, business logic in model
     */
    public boolean handleDeposit(String accountNumber, double amount) {
        // Input validation
        if (accountNumber == null || accountNumber.isEmpty()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        // Delegate to business logic layer
        return bankSystem.deposit(accountNumber, amount);
    }
    
    /**
     * Handle withdrawal request
     */
    public boolean handleWithdraw(String accountNumber, double amount) {
        // Input validation
        if (accountNumber == null || accountNumber.isEmpty()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        // Delegate to business logic layer
        return bankSystem.withdraw(accountNumber, amount);
    }
    
    /**
     * Handle transfer request
     */
    public boolean handleTransfer(String fromAccount, String toAccount, double amount) {
        // Input validation
        if (fromAccount == null || toAccount == null || 
            fromAccount.isEmpty() || toAccount.isEmpty()) {
            return false;
        }
        
        if (amount <= 0) {
            return false;
        }
        
        if (fromAccount.equals(toAccount)) {
            return false; // Can't transfer to same account
        }
        
        // Delegate to business logic layer
        return bankSystem.transfer(fromAccount, toAccount, amount);
    }
    
    /**
     * Handle create account request
     */
    public boolean handleCreateAccount(String customerId, String accountType, double initialDeposit) {
        // Input validation
        if (customerId == null || accountType == null) {
            return false;
        }
        
        // Delegate to business logic layer based on type
        switch (accountType) {
            case "Savings":
                return bankSystem.createSavingsAccount(customerId, initialDeposit) != null;
            case "Cheque":
                AccountClass cheque = bankSystem.createChequeAccount(customerId);
                if (cheque != null && initialDeposit > 0) {
                    bankSystem.deposit(cheque.getAccountNumber(), initialDeposit);
                }
                return cheque != null;
            case "Investment":
                return bankSystem.createInvestmentAccount(customerId, "Fixed Deposit", 
                                                         initialDeposit) != null;
            default:
                return false;
        }
    }
    
    /**
     * Get customer accounts - delegates to DAO
     */
    public List<AccountClass> getCustomerAccounts(String customerId) {
        AccountDAO accountDAO = new AccountDAO();
        return accountDAO.getAccountsByCustomerId(customerId);
    }
    
    /**
     * Get account by number - delegates to business logic
     */
    public AccountClass getAccountByNumber(String accountNumber) {
        return bankSystem.findAccountByNumber(accountNumber);
    }
    
    /**
     * Get recent transactions - delegates to DAO
     */
    public List<TransactionDAO.Transaction> getRecentTransactions(String accountNumber, int limit) {
        TransactionDAO transactionDAO = new TransactionDAO();
        return transactionDAO.getRecentTransactions(accountNumber, limit);
    }
    
    /**
     * Show account statement
     */
    public void showAccountStatement(String accountNumber) {
        bankSystem.printAccountStatement(accountNumber);
    }
    
    /**
     * Handle logout
     */
    public void handleLogout() {
        LoginController loginController = new LoginController(stage, bankSystem);
        loginController.showLoginView();
    }
}