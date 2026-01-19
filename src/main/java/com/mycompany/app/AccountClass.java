public abstract class AccountClass {
    private String accountNumber;
    private double balance;
    private String accountType;
    private String customerId;
    
    public AccountClass(String accountNumber, String customerId, String accountType) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = 0.0;
    }
    
    public AccountClass(String accountNumber, String customerId, String accountType, double initialBalance) {
        this.accountNumber = accountNumber;
        this.customerId = customerId;
        this.accountType = accountType;
        this.balance = initialBalance;
    }
    
    // Getters and Setters
    public String getAccountNumber() {
        return accountNumber;
    }
    
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }
    
    public double getBalance() {
        return balance;
    }
    
    protected void setBalance(double balance) {
        this.balance = balance;
    }
    
    public String getAccountType() {
        return accountType;
    }
    
    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }
    
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    // Business Logic
    public boolean deposit(double amount) {
        if (amount > 0) {
            balance += amount;
            return true;
        }
        return false;
    }
    
    public abstract boolean withdraw(double amount);
    
    @Override
    public String toString() {
        return "Account [Number=" + accountNumber + ", Type=" + accountType + 
               ", Balance=" + String.format("%.2f", balance) + "]";
    }
}