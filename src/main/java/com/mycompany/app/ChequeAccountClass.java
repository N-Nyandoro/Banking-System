public class ChequeAccountClass extends AccountClass implements WithdrawableInterface {
    private double overdraftLimit;
    private double withdrawalLimit;
    
    public ChequeAccountClass(String accountNumber, String customerId) {
        super(accountNumber, customerId, "Cheque");
        this.overdraftLimit = 1000.0; // Default overdraft limit
        this.withdrawalLimit = 5000.0; // Default withdrawal limit
    }
    
    public ChequeAccountClass(String accountNumber, String customerId, double initialBalance) {
        super(accountNumber, customerId, "Cheque", initialBalance);
        this.overdraftLimit = 1000.0;
        this.withdrawalLimit = 5000.0;
    }
    
    // Getters and Setters
    public double getOverdraftLimit() {
        return overdraftLimit;
    }
    
    public void setOverdraftLimit(double overdraftLimit) {
        this.overdraftLimit = overdraftLimit;
    }
    
    @Override
    public double getWithdrawalLimit() {
        return withdrawalLimit;
    }
    
    @Override
    public void setWithdrawalLimit(double limit) {
        this.withdrawalLimit = limit;
    }
    
    // Business Logic
    @Override
    public boolean withdraw(double amount) {
        if (amount <= 0) {
            return false;
        }
        
        if (amount > withdrawalLimit) {
            return false; // Exceeds withdrawal limit
        }
        
        double availableBalance = getBalance() + overdraftLimit;
        if (amount <= availableBalance) {
            setBalance(getBalance() - amount);
            return true;
        }
        
        return false; // Insufficient funds including overdraft
    }
    
    @Override
    public String toString() {
        return super.toString() + " [Overdraft Limit=" + 
               String.format("%.2f", overdraftLimit) + "]";
    }
}