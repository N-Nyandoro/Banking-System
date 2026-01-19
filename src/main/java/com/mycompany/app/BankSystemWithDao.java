public class BankSystemWithDAO {
    private String bankName;
    private CustomerDAO customerDAO;
    private AccountDAO accountDAO;
    private TransactionDAO transactionDAO;
    private int nextCustomerId;
    private int nextAccountNumber;
    
    public BankSystemWithDAO(String bankName) {
        this.bankName = bankName;
        this.customerDAO = new CustomerDAO();
        this.accountDAO = new AccountDAO();
        this.transactionDAO = new TransactionDAO();
        this.nextCustomerId = 1001;
        this.nextAccountNumber = 10001;
    }
    
    // Getters
    public String getBankName() {
        return bankName;
    }
    
    public void setBankName(String bankName) {
        this.bankName = bankName;
    }
    
    // Customer Management with Database Persistence
    public CustomerClass createCustomer(String firstName, String lastName, 
                                       String email, String phone, String address) {
        // Check if email already exists
        if (customerDAO.emailExists(email)) {
            System.err.println("Error: Email already exists!");
            return null;
        }
        
        String customerId = "CUST" + nextCustomerId++;
        CustomerClass customer = new CustomerClass(customerId, firstName, lastName, 
                                                   email, phone, address);
        
        if (customerDAO.createCustomer(customer)) {
            System.out.println("Customer created successfully in database: " + customerId);
            return customer;
        } else {
            System.err.println("Failed to create customer in database");
            return null;
        }
    }
    
    public CustomerClass findCustomerById(String customerId) {
        return customerDAO.getCustomerById(customerId);
    }
    
    public CustomerClass findCustomerByEmail(String email) {
        return customerDAO.getCustomerByEmail(email);
    }
    
    public boolean updateCustomer(CustomerClass customer) {
        return customerDAO.updateCustomer(customer);
    }
    
    public boolean removeCustomer(String customerId) {
        return customerDAO.deleteCustomer(customerId);
    }
    
    // Account Management with Database Persistence
    public ChequeAccountClass createChequeAccount(String customerId) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer == null) {
            System.err.println("Error: Customer not found!");
            return null;
        }
        
        String accountNumber = "CHQ" + nextAccountNumber++;
        ChequeAccountClass account = new ChequeAccountClass(accountNumber, customerId);
        
        if (accountDAO.createAccount(account)) {
            System.out.println("Cheque account created successfully: " + accountNumber);
            return account;
        } else {
            System.err.println("Failed to create cheque account in database");
            return null;
        }
    }
    
    public SavingsAccountClass createSavingsAccount(String customerId, double initialDeposit) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer == null) {
            System.err.println("Error: Customer not found!");
            return null;
        }
        
        if (initialDeposit < 100.0) {
            System.err.println("Error: Minimum initial deposit for savings account is 100.00");
            return null;
        }
        
        String accountNumber = "SAV" + nextAccountNumber++;
        SavingsAccountClass account = new SavingsAccountClass(accountNumber, customerId, 
                                                              initialDeposit);
        
        if (accountDAO.createAccount(account)) {
            // Record initial deposit transaction
            transactionDAO.recordTransaction(accountNumber, "DEPOSIT", initialDeposit, 
                                           initialDeposit, "Initial deposit");
            System.out.println("Savings account created successfully: " + accountNumber);
            return account;
        } else {
            System.err.println("Failed to create savings account in database");
            return null;
        }
    }
    
    public InvestmentAccountClass createInvestmentAccount(String customerId, 
                                                          String investmentType, 
                                                          double initialDeposit) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer == null) {
            System.err.println("Error: Customer not found!");
            return null;
        }
        
        if (initialDeposit <= 0) {
            System.err.println("Error: Initial deposit must be greater than 0");
            return null;
        }
        
        String accountNumber = "INV" + nextAccountNumber++;
        InvestmentAccountClass account = new InvestmentAccountClass(accountNumber, 
                                                                    customerId, 
                                                                    investmentType, 
                                                                    initialDeposit);
        
        if (accountDAO.createAccount(account)) {
            // Record initial deposit transaction
            transactionDAO.recordTransaction(accountNumber, "DEPOSIT", initialDeposit, 
                                           initialDeposit, "Initial investment");
            System.out.println("Investment account created successfully: " + accountNumber);
            return account;
        } else {
            System.err.println("Failed to create investment account in database");
            return null;
        }
    }
    
    public AccountClass findAccountByNumber(String accountNumber) {
        return accountDAO.getAccountByNumber(accountNumber);
    }
    
    public boolean removeAccount(String accountNumber) {
        return accountDAO.deleteAccount(accountNumber);
    }
    
    // Transaction Operations with Database Persistence
    public boolean deposit(String accountNumber, double amount) {
        AccountClass account = findAccountByNumber(accountNumber);
        if (account == null) {
            System.err.println("Error: Account not found!");
            return false;
        }
        
        if (account.deposit(amount)) {
            // Update database
            accountDAO.updateAccountBalance(accountNumber, account.getBalance());
            // Record transaction
            transactionDAO.recordTransaction(accountNumber, "DEPOSIT", amount, 
                                           account.getBalance(), "Deposit");
            System.out.println("Deposit successful: " + amount);
            return true;
        }
        
        return false;
    }
    
    public boolean withdraw(String accountNumber, double amount) {
        AccountClass account = findAccountByNumber(accountNumber);
        if (account == null) {
            System.err.println("Error: Account not found!");
            return false;
        }
        
        if (account.withdraw(amount)) {
            // Update database
            accountDAO.updateAccountBalance(accountNumber, account.getBalance());
            // Record transaction
            transactionDAO.recordTransaction(accountNumber, "WITHDRAWAL", amount, 
                                           account.getBalance(), "Withdrawal");
            System.out.println("Withdrawal successful: " + amount);
            return true;
        } else {
            System.err.println("Withdrawal failed: Insufficient funds or exceeds limits");
            return false;
        }
    }
    
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        AccountClass fromAccount = findAccountByNumber(fromAccountNumber);
        AccountClass toAccount = findAccountByNumber(toAccountNumber);
        
        if (fromAccount == null || toAccount == null) {
            System.err.println("Error: One or both accounts not found!");
            return false;
        }
        
        if (fromAccount.withdraw(amount)) {
            if (toAccount.deposit(amount)) {
                // Update both accounts in database
                accountDAO.updateAccountBalance(fromAccountNumber, fromAccount.getBalance());
                accountDAO.updateAccountBalance(toAccountNumber, toAccount.getBalance());
                
                // Record transactions for both accounts
                transactionDAO.recordTransaction(fromAccountNumber, "TRANSFER_OUT", amount, 
                                               fromAccount.getBalance(), 
                                               "Transfer to " + toAccountNumber, 
                                               toAccountNumber);
                transactionDAO.recordTransaction(toAccountNumber, "TRANSFER_IN", amount, 
                                               toAccount.getBalance(), 
                                               "Transfer from " + fromAccountNumber, 
                                               fromAccountNumber);
                
                System.out.println("Transfer successful: " + amount);
                return true;
            } else {
                // Rollback withdrawal if deposit fails
                fromAccount.deposit(amount);
            }
        }
        
        System.err.println("Transfer failed");
        return false;
    }
    
    // Interest Calculation for all eligible accounts
    public void calculateInterestForAllAccounts() {
        var accounts = accountDAO.getAllAccounts();
        int count = 0;
        
        for (AccountClass account : accounts) {
            if (account instanceof InterestBearingInterface) {
                double oldBalance = account.getBalance();
                ((InterestBearingInterface) account).calculateInterest();
                double newBalance = account.getBalance();
                double interest = newBalance - oldBalance;
                
                // Update database
                accountDAO.updateAccountBalance(account.getAccountNumber(), newBalance);
                // Record transaction
                transactionDAO.recordTransaction(account.getAccountNumber(), "INTEREST", 
                                               interest, newBalance, "Interest credited");
                count++;
            }
        }
        
        System.out.println("Interest calculated for " + count + " accounts");
    }
    
    // Reporting Methods
    public double getTotalBankBalance() {
        double total = 0.0;
        for (AccountClass account : accountDAO.getAllAccounts()) {
            total += account.getBalance();
        }
        return total;
    }
    
    public int getTotalCustomers() {
        return customerDAO.getCustomerCount();
    }
    
    public int getTotalAccounts() {
        return accountDAO.getAccountCount();
    }
    
    // Get customer's complete information with accounts
    public CustomerClass getCustomerWithAccounts(String customerId) {
        CustomerClass customer = customerDAO.getCustomerById(customerId);
        if (customer != null) {
            var accounts = accountDAO.getAccountsByCustomerId(customerId);
            for (AccountClass account : accounts) {
                customer.addAccount(account);
            }
        }
        return customer;
    }
    
    // Get account transaction history
    public void printAccountStatement(String accountNumber) {
        AccountClass account = findAccountByNumber(accountNumber);
        if (account == null) {
            System.out.println("Account not found!");
            return;
        }
        
        System.out.println("\n=== Account Statement ===");
        System.out.println("Account Number: " + accountNumber);
        System.out.println("Account Type: " + account.getAccountType());
        System.out.println("Current Balance: " + String.format("%.2f", account.getBalance()));
        System.out.println("\nTransaction History:");
        
        var transactions = transactionDAO.getTransactionsByAccountNumber(accountNumber);
        if (transactions.isEmpty()) {
            System.out.println("No transactions found.");
        } else {
            for (var transaction : transactions) {
                System.out.println(transaction);
            }
        }
        System.out.println("========================\n");
    }
    
    @Override
    public String toString() {
        return "Bank [Name=" + bankName + ", Customers=" + getTotalCustomers() + 
               ", Accounts=" + getTotalAccounts() + ", Total Balance=" + 
               String.format("%.2f", getTotalBankBalance()) + "]";
    }
}
