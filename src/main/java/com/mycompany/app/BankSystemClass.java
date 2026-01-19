import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

public class BankSystemClass {
    private String bankName;
    private List<CustomerClass> customers;
    private Map<String, AccountClass> accounts;
    private int nextCustomerId;
    private int nextAccountNumber;
    
    public BankSystemClass(String bankName) {
        this.bankName = bankName;
        this.customers = new ArrayList<>();
        this.accounts = new HashMap<>();
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
    
    public List<CustomerClass> getCustomers() {
        return customers;
    }
    
    public Map<String, AccountClass> getAccounts() {
        return accounts;
    }
    
    // Customer Management
    public CustomerClass createCustomer(String firstName, String lastName, 
                                       String email, String phone, String address) {
        String customerId = "CUST" + nextCustomerId++;
        CustomerClass customer = new CustomerClass(customerId, firstName, lastName, 
                                                   email, phone, address);
        customers.add(customer);
        return customer;
    }
    
    public CustomerClass findCustomerById(String customerId) {
        for (CustomerClass customer : customers) {
            if (customer.getCustomerId().equals(customerId)) {
                return customer;
            }
        }
        return null;
    }
    
    public boolean removeCustomer(String customerId) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer != null) {
            // Remove all accounts associated with this customer
            List<AccountClass> customerAccounts = new ArrayList<>(customer.getAccounts());
            for (AccountClass account : customerAccounts) {
                removeAccount(account.getAccountNumber());
            }
            customers.remove(customer);
            return true;
        }
        return false;
    }
    
    // Account Management
    public ChequeAccountClass createChequeAccount(String customerId) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer != null) {
            String accountNumber = "CHQ" + nextAccountNumber++;
            ChequeAccountClass account = new ChequeAccountClass(accountNumber, customerId);
            accounts.put(accountNumber, account);
            customer.addAccount(account);
            return account;
        }
        return null;
    }
    
    public SavingsAccountClass createSavingsAccount(String customerId, double initialDeposit) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer != null && initialDeposit >= 100.0) {
            String accountNumber = "SAV" + nextAccountNumber++;
            SavingsAccountClass account = new SavingsAccountClass(accountNumber, customerId, 
                                                                  initialDeposit);
            accounts.put(accountNumber, account);
            customer.addAccount(account);
            return account;
        }
        return null;
    }
    
    public InvestmentAccountClass createInvestmentAccount(String customerId, 
                                                          String investmentType, 
                                                          double initialDeposit) {
        CustomerClass customer = findCustomerById(customerId);
        if (customer != null && initialDeposit > 0) {
            String accountNumber = "INV" + nextAccountNumber++;
            InvestmentAccountClass account = new InvestmentAccountClass(accountNumber, 
                                                                        customerId, 
                                                                        investmentType, 
                                                                        initialDeposit);
            accounts.put(accountNumber, account);
            customer.addAccount(account);
            return account;
        }
        return null;
    }
    
    public AccountClass findAccountByNumber(String accountNumber) {
        return accounts.get(accountNumber);
    }
    
    public boolean removeAccount(String accountNumber) {
        AccountClass account = accounts.remove(accountNumber);
        if (account != null) {
            CustomerClass customer = findCustomerById(account.getCustomerId());
            if (customer != null) {
                customer.removeAccount(account);
            }
            return true;
        }
        return false;
    }
    
    // Transaction Operations
    public boolean deposit(String accountNumber, double amount) {
        AccountClass account = findAccountByNumber(accountNumber);
        if (account != null) {
            return account.deposit(amount);
        }
        return false;
    }
    
    public boolean withdraw(String accountNumber, double amount) {
        AccountClass account = findAccountByNumber(accountNumber);
        if (account != null) {
            return account.withdraw(amount);
        }
        return false;
    }
    
    public boolean transfer(String fromAccountNumber, String toAccountNumber, double amount) {
        AccountClass fromAccount = findAccountByNumber(fromAccountNumber);
        AccountClass toAccount = findAccountByNumber(toAccountNumber);
        
        if (fromAccount != null && toAccount != null) {
            if (fromAccount.withdraw(amount)) {
                if (toAccount.deposit(amount)) {
                    return true;
                } else {
                    // Rollback withdrawal if deposit fails
                    fromAccount.deposit(amount);
                }
            }
        }
        return false;
    }
    
    // Interest Calculation for all eligible accounts
    public void calculateInterestForAllAccounts() {
        for (AccountClass account : accounts.values()) {
            if (account instanceof InterestBearingInterface) {
                ((InterestBearingInterface) account).calculateInterest();
            }
        }
    }
    
    // Reporting
    public double getTotalBankBalance() {
        double total = 0.0;
        for (AccountClass account : accounts.values()) {
            total += account.getBalance();
        }
        return total;
    }
    
    public int getTotalCustomers() {
        return customers.size();
    }
    
    public int getTotalAccounts() {
        return accounts.size();
    }
    
    @Override
    public String toString() {
        return "Bank [Name=" + bankName + ", Customers=" + customers.size() + 
               ", Accounts=" + accounts.size() + ", Total Balance=" + 
               String.format("%.2f", getTotalBankBalance()) + "]";
    }
}