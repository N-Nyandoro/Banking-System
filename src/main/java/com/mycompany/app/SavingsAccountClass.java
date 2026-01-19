public class SavingsAccountClass extends AccountClass 
        implements WithdrawableInterface, InterestBearingInterface {
    private double interestRate;
    private double withdrawalLimit;
    private double minimumBalance;
    
    public SavingsAccountClass(String accountNumber, String customerId) {
        super(accountNumber, customerId, "Savings");
        this.interestRate = 0.03; // 3% default interest rate
        this.withdrawalLimit = 10000.0; // Default withdrawal limit
        this.minimumBalance = 100.0; // Minimum balance requirement
    }
    
    public SavingsAccountClass(String accountNumber, String customerId, double initialBalance) {
        super(accountNumber, customerId, "Savings", initialBalance);
        this.interestRate = 0.03;
        this.withdrawalLimit = 10000.0;
        this.minimumBalance = 100.0;
    }
    
    // Getters and Setters
    @Override
    public double getInterestRate() {
        return interestRate;
    }
    
    @Override
    public void setInterestRate(double rate) {
        this.interestRate = rate;
    }
    
    @Override
    public double getWithdrawalLimit() {
        return withdrawalLimit;
    }
    
    @Override
    public void setWithdrawalLimit(double limit) {
        this.withdrawalLimit = limit;
    }
    
    public double getMinimumBalance() {
        return minimumBalance;
    }
    
    public void setMinimumBalance(double minimumBalance) {
        this.minimumBalance = minimumBalance;
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
        
        double remainingBalance = getBalance() - amount;
        if (remainingBalance >= minimumBalance) {
            setBalance(remainingBalance);
            return true;
        }
        
        return false; // Would violate minimum balance requirement
    }
    
    @Override
    public void calculateInterest() {
        double interest = getBalance() * interestRate;
        deposit(interest);
    }
    
    @Override
    public String toString() {
        return super.toString() + " [Interest Rate=" + 
               String.format("%.2f%%", interestRate * 100) + 
               ", Min Balance=" + String.format("%.2f", minimumBalance) + "]";
    }
}