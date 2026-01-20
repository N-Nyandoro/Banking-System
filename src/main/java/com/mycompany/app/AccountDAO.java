import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class AccountDAO {
    
    //Create a new account in the database
    public boolean createAccount(AccountClass account) {
        String sql = "INSERT INTO accounts (account_number, customer_id, account_type, balance, " +
                     "interest_rate, withdrawal_limit, overdraft_limit, minimum_balance, " +
                     "investment_type, term_months) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, account.getAccountNumber());
            pstmt.setString(2, account.getCustomerId());
            pstmt.setString(3, account.getAccountType());
            pstmt.setDouble(4, account.getBalance());
            
            // Set type-specific attributes
            if (account instanceof SavingsAccountClass) {
                SavingsAccountClass savings = (SavingsAccountClass) account;
                pstmt.setDouble(5, savings.getInterestRate());
                pstmt.setDouble(6, savings.getWithdrawalLimit());
                pstmt.setNull(7, Types.DECIMAL);
                pstmt.setDouble(8, savings.getMinimumBalance());
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.INTEGER);
            } else if (account instanceof ChequeAccountClass) {
                ChequeAccountClass cheque = (ChequeAccountClass) account;
                pstmt.setNull(5, Types.DECIMAL);
                pstmt.setDouble(6, cheque.getWithdrawalLimit());
                pstmt.setDouble(7, cheque.getOverdraftLimit());
                pstmt.setNull(8, Types.DECIMAL);
                pstmt.setNull(9, Types.VARCHAR);
                pstmt.setNull(10, Types.INTEGER);
            } else if (account instanceof InvestmentAccountClass) {
                InvestmentAccountClass investment = (InvestmentAccountClass) account;
                pstmt.setDouble(5, investment.getInterestRate());
                pstmt.setNull(6, Types.DECIMAL);
                pstmt.setNull(7, Types.DECIMAL);
                pstmt.setNull(8, Types.DECIMAL);
                pstmt.setString(9, investment.getInvestmentType());
                pstmt.setInt(10, investment.getTermMonths());
            } else {
                // Generic account
                for (int i = 5; i <= 10; i++) {
                    pstmt.setNull(i, Types.NULL);
                }
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error creating account: " + e.getMessage());
            return false;
        }
    }
    
    //Retrieve an account by account number
    public AccountClass getAccountByNumber(String accountNumber) {
        String sql = "SELECT * FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractAccountFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving account: " + e.getMessage());
        }
        
        return null;
    }
    
    //Retrieve all accounts for a specific customer
    public List<AccountClass> getAccountsByCustomerId(String customerId) {
        List<AccountClass> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE customer_id = ? ORDER BY account_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving customer accounts: " + e.getMessage());
        }
        
        return accounts;
    }
    
    //Retrieve all accounts
    public List<AccountClass> getAllAccounts() {
        List<AccountClass> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts ORDER BY account_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving all accounts: " + e.getMessage());
        }
        
        return accounts;
    }
    
    //Update account balance
    public boolean updateAccountBalance(String accountNumber, double newBalance) {
        String sql = "UPDATE accounts SET balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, newBalance);
            pstmt.setString(2, accountNumber);
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating account balance: " + e.getMessage());
            return false;
        }
    }
    
    //Update complete account information
    public boolean updateAccount(AccountClass account) {
        String sql = "UPDATE accounts SET balance = ?, interest_rate = ?, withdrawal_limit = ?, " +
                     "overdraft_limit = ?, minimum_balance = ? WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setDouble(1, account.getBalance());
            
            if (account instanceof SavingsAccountClass) {
                SavingsAccountClass savings = (SavingsAccountClass) account;
                pstmt.setDouble(2, savings.getInterestRate());
                pstmt.setDouble(3, savings.getWithdrawalLimit());
                pstmt.setNull(4, Types.DECIMAL);
                pstmt.setDouble(5, savings.getMinimumBalance());
            } else if (account instanceof ChequeAccountClass) {
                ChequeAccountClass cheque = (ChequeAccountClass) account;
                pstmt.setNull(2, Types.DECIMAL);
                pstmt.setDouble(3, cheque.getWithdrawalLimit());
                pstmt.setDouble(4, cheque.getOverdraftLimit());
                pstmt.setNull(5, Types.DECIMAL);
            } else if (account instanceof InvestmentAccountClass) {
                InvestmentAccountClass investment = (InvestmentAccountClass) account;
                pstmt.setDouble(2, investment.getInterestRate());
                pstmt.setNull(3, Types.DECIMAL);
                pstmt.setNull(4, Types.DECIMAL);
                pstmt.setNull(5, Types.DECIMAL);
            }
            
            pstmt.setString(6, account.getAccountNumber());
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error updating account: " + e.getMessage());
            return false;
        }
    }
    
    //Delete an account
    public boolean deleteAccount(String accountNumber) {
        String sql = "DELETE FROM accounts WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error deleting account: " + e.getMessage());
            return false;
        }
    }
    
    //Get accounts by type
    public List<AccountClass> getAccountsByType(String accountType) {
        List<AccountClass> accounts = new ArrayList<>();
        String sql = "SELECT * FROM accounts WHERE account_type = ? ORDER BY account_number";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                accounts.add(extractAccountFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving accounts by type: " + e.getMessage());
        }
        
        return accounts;
    }
    
    //Get total balance for a customer
    public double getTotalBalanceByCustomerId(String customerId) {
        String sql = "SELECT SUM(balance) FROM accounts WHERE customer_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total balance: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    //Get total number of accounts
    public int getAccountCount() {
        String sql = "SELECT COUNT(*) FROM accounts";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting accounts: " + e.getMessage());
        }
        
        return 0;
    }
    
    //Helper method to extract account from ResultSet
    private AccountClass extractAccountFromResultSet(ResultSet rs) throws SQLException {
        String accountNumber = rs.getString("account_number");
        String customerId = rs.getString("customer_id");
        String accountType = rs.getString("account_type");
        double balance = rs.getDouble("balance");
        
        AccountClass account = null;
        
        switch (accountType) {
            case "Savings":
                account = new SavingsAccountClass(accountNumber, customerId, balance);
                double interestRate = rs.getDouble("interest_rate");
                double withdrawalLimit = rs.getDouble("withdrawal_limit");
                double minimumBalance = rs.getDouble("minimum_balance");
                ((SavingsAccountClass) account).setInterestRate(interestRate);
                ((SavingsAccountClass) account).setWithdrawalLimit(withdrawalLimit);
                ((SavingsAccountClass) account).setMinimumBalance(minimumBalance);
                break;
                
            case "Cheque":
                account = new ChequeAccountClass(accountNumber, customerId, balance);
                double withdrawalLimitCheque = rs.getDouble("withdrawal_limit");
                double overdraftLimit = rs.getDouble("overdraft_limit");
                ((ChequeAccountClass) account).setWithdrawalLimit(withdrawalLimitCheque);
                ((ChequeAccountClass) account).setOverdraftLimit(overdraftLimit);
                break;
                
            case "Investment":
                String investmentType = rs.getString("investment_type");
                account = new InvestmentAccountClass(accountNumber, customerId, investmentType, balance);
                double investmentInterestRate = rs.getDouble("interest_rate");
                int termMonths = rs.getInt("term_months");
                ((InvestmentAccountClass) account).setInterestRate(investmentInterestRate);
                ((InvestmentAccountClass) account).setTermMonths(termMonths);
                break;
        }
        
        return account;
    }
}
