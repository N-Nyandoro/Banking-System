import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;

/**
 * Data Access Object for Transaction operations
 * Handles all database operations for transactions
 */
public class TransactionDAO {
    
    /**
     * Transaction class to store transaction details
     */
    public static class Transaction {
        private int transactionId;
        private String accountNumber;
        private String transactionType;
        private double amount;
        private double balanceAfter;
        private String description;
        private Timestamp transactionDate;
        private String toAccountNumber;
        
        public Transaction(int transactionId, String accountNumber, String transactionType,
                          double amount, double balanceAfter, String description,
                          Timestamp transactionDate, String toAccountNumber) {
            this.transactionId = transactionId;
            this.accountNumber = accountNumber;
            this.transactionType = transactionType;
            this.amount = amount;
            this.balanceAfter = balanceAfter;
            this.description = description;
            this.transactionDate = transactionDate;
            this.toAccountNumber = toAccountNumber;
        }
        
        // Getters
        public int getTransactionId() { return transactionId; }
        public String getAccountNumber() { return accountNumber; }
        public String getTransactionType() { return transactionType; }
        public double getAmount() { return amount; }
        public double getBalanceAfter() { return balanceAfter; }
        public String getDescription() { return description; }
        public Timestamp getTransactionDate() { return transactionDate; }
        public String getToAccountNumber() { return toAccountNumber; }
        
        @Override
        public String toString() {
            return String.format("[%d] %s: %.2f | Balance: %.2f | %s | %s",
                    transactionId, transactionType, amount, balanceAfter,
                    transactionDate, description != null ? description : "");
        }
    }
    
    /**
     * Record a new transaction in the database
     */
    public boolean recordTransaction(String accountNumber, String transactionType,
                                    double amount, double balanceAfter, String description) {
        return recordTransaction(accountNumber, transactionType, amount, balanceAfter, 
                                description, null);
    }
    
    /**
     * Record a new transaction with optional to_account (for transfers)
     */
    public boolean recordTransaction(String accountNumber, String transactionType,
                                    double amount, double balanceAfter, String description,
                                    String toAccountNumber) {
        String sql = "INSERT INTO transactions (account_number, transaction_type, amount, " +
                     "balance_after, description, to_account_number) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, transactionType);
            pstmt.setDouble(3, amount);
            pstmt.setDouble(4, balanceAfter);
            pstmt.setString(5, description);
            if (toAccountNumber != null) {
                pstmt.setString(6, toAccountNumber);
            } else {
                pstmt.setNull(6, Types.VARCHAR);
            }
            
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Get transaction by ID
     */
    public Transaction getTransactionById(int transactionId) {
        String sql = "SELECT * FROM transactions WHERE transaction_id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, transactionId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return extractTransactionFromResultSet(rs);
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transaction: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Get all transactions for a specific account
     */
    public List<Transaction> getTransactionsByAccountNumber(String accountNumber) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Get all transactions for a specific account within a date range
     */
    public List<Transaction> getTransactionsByDateRange(String accountNumber,
                                                        Timestamp startDate,
                                                        Timestamp endDate) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "AND transaction_date BETWEEN ? AND ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setTimestamp(2, startDate);
            pstmt.setTimestamp(3, endDate);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions by date: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Get all transactions by type
     */
    public List<Transaction> getTransactionsByType(String accountNumber, String transactionType) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "AND transaction_type = ? ORDER BY transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setString(2, transactionType);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving transactions by type: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Get recent transactions (last N transactions)
     */
    public List<Transaction> getRecentTransactions(String accountNumber, int limit) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE account_number = ? " +
                     "ORDER BY transaction_date DESC LIMIT ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.setInt(2, limit);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving recent transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Get all transactions for all accounts of a customer
     */
    public List<Transaction> getCustomerTransactions(String customerId) {
        List<Transaction> transactions = new ArrayList<>();
        String sql = "SELECT t.* FROM transactions t " +
                     "JOIN accounts a ON t.account_number = a.account_number " +
                     "WHERE a.customer_id = ? ORDER BY t.transaction_date DESC";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                transactions.add(extractTransactionFromResultSet(rs));
            }
            
        } catch (SQLException e) {
            System.err.println("Error retrieving customer transactions: " + e.getMessage());
        }
        
        return transactions;
    }
    
    /**
     * Get transaction count for an account
     */
    public int getTransactionCount(String accountNumber) {
        String sql = "SELECT COUNT(*) FROM transactions WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error counting transactions: " + e.getMessage());
        }
        
        return 0;
    }
    
    /**
     * Calculate total deposits for an account
     */
    public double getTotalDeposits(String accountNumber) {
        String sql = "SELECT SUM(amount) FROM transactions " +
                     "WHERE account_number = ? AND transaction_type = 'DEPOSIT'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total deposits: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Calculate total withdrawals for an account
     */
    public double getTotalWithdrawals(String accountNumber) {
        String sql = "SELECT SUM(amount) FROM transactions " +
                     "WHERE account_number = ? AND transaction_type = 'WITHDRAWAL'";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return rs.getDouble(1);
            }
            
        } catch (SQLException e) {
            System.err.println("Error calculating total withdrawals: " + e.getMessage());
        }
        
        return 0.0;
    }
    
    /**
     * Delete all transactions for an account (used when closing an account)
     */
    public boolean deleteTransactionsByAccount(String accountNumber) {
        String sql = "DELETE FROM transactions WHERE account_number = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, accountNumber);
            pstmt.executeUpdate();
            return true;
            
        } catch (SQLException e) {
            System.err.println("Error deleting transactions: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to extract transaction from ResultSet
     */
    private Transaction extractTransactionFromResultSet(ResultSet rs) throws SQLException {
        int transactionId = rs.getInt("transaction_id");
        String accountNumber = rs.getString("account_number");
        String transactionType = rs.getString("transaction_type");
        double amount = rs.getDouble("amount");
        double balanceAfter = rs.getDouble("balance_after");
        String description = rs.getString("description");
        Timestamp transactionDate = rs.getTimestamp("transaction_date");
        String toAccountNumber = rs.getString("to_account_number");
        
        return new Transaction(transactionId, accountNumber, transactionType, amount,
                              balanceAfter, description, transactionDate, toAccountNumber);
    }
}