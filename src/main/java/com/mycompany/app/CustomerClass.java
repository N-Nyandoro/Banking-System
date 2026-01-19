import java.util.ArrayList;
import java.util.List;

public class CustomerClass {
    private String customerId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private List<AccountClass> accounts;
    
    public CustomerClass(String customerId, String firstName, String lastName) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.accounts = new ArrayList<>();
    }
    
    public CustomerClass(String customerId, String firstName, String lastName, 
                        String email, String phone, String address) {
        this.customerId = customerId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.address = address;
        this.accounts = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public List<AccountClass> getAccounts() {
        return accounts;
    }
    
    // Business Logic
    public void addAccount(AccountClass account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }
    
    public void removeAccount(AccountClass account) {
        accounts.remove(account);
    }
    
    public AccountClass findAccountByNumber(String accountNumber) {
        for (AccountClass account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                return account;
            }
        }
        return null;
    }
    
    public double getTotalBalance() {
        double total = 0.0;
        for (AccountClass account : accounts) {
            total += account.getBalance();
        }
        return total;
    }
    
    @Override
    public String toString() {
        return "Customer [ID=" + customerId + ", Name=" + getFullName() + 
               ", Email=" + email + ", Accounts=" + accounts.size() + "]";
    }
}