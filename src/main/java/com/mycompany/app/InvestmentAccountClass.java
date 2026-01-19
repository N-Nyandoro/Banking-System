public class InvestmentAccountClass extends AccountClass implements InterestBearingInterface {
    private double interestRate;
    private String investmentType;
    private int termMonths; // Lock-in period in months
    
    public InvestmentAccountClass(String accountNumber, String customerId, String investmentType) {
        super(accountNumber, customerId, "Investment");
        this.investmentType = investmentType;
        this.interestRate = 0.05; // 5% default interest rate
        this.termMonths = 12; // Default 12-month term
    }
    
    public InvestmentAccountClass(String accountNumber, String customerId, 
                                  String investmentType, double initialBalance) {
        super(accountNumber, customerId, "Investment", initialBalance);
        this.investmentType = investmentType;
        this.interestRate = 0.05;
        this.termMonths = 12;
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
    
    public String getInvestmentType() {
        return investmentType;
    }
    
    public void setInvestmentType(String investmentType) {
        this.investmentType = investmentType;
    }
    
    public int getTermMonths() {
        return termMonths;
    }
    
    public void setTermMonths(int termMonths) {
        this.termMonths = termMonths;
    }
    
    // Business Logic
    @Override
    public boolean withdraw(double amount) {
        // Investment accounts typically don't allow withdrawals during term
        // Only allow full withdrawal
        if (amount == getBalance()) {
            setBalance(0);
            return true;
        }
        return false;
    }
    
    @Override
    public void calculateInterest() {
        // Calculate compound interest for investment accounts
        double interest = getBalance() * interestRate;
        deposit(interest);
    }
    
    public void calculateCompoundInterest() {
        // Monthly compound interest calculation
        double monthlyRate = interestRate / 12;
        double compoundedAmount = getBalance() * Math.pow(1 + monthlyRate, termMonths);
        double interest = compoundedAmount - getBalance();
        deposit(interest);
    }
    
    @Override
    public String toString() {
        return super.toString() + " [Investment Type=" + investmentType + 
               ", Interest Rate=" + String.format("%.2f%%", interestRate * 100) + 
               ", Term=" + termMonths + " months]";
    }
}