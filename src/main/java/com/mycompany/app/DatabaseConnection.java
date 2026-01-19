import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Manages database connections for the banking system.
 * Supports H2, SQLite, MySQL, and PostgreSQL databases.
 */
public class DatabaseConnection {
    // Database configuration
    private static final String DB_TYPE = "H2"; // Change to "SQLITE", "MYSQL", or "POSTGRESQL"
    
    // H2 Database (In-memory or file-based)
    private static final String H2_URL = "jdbc:h2:./bankdb;AUTO_SERVER=TRUE";
    private static final String H2_USER = "sa";
    private static final String H2_PASSWORD = "";
    
    // SQLite Database
    private static final String SQLITE_URL = "jdbc:sqlite:bankdb.db";
    
    // MySQL Database
    private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/bankdb";
    private static final String MYSQL_USER = "root";
    private static final String MYSQL_PASSWORD = "password";
    
    // PostgreSQL Database
    private static final String POSTGRESQL_URL = "jdbc:postgresql://localhost:5432/bankdb";
    private static final String POSTGRESQL_USER = "postgres";
    private static final String POSTGRESQL_PASSWORD = "password";
    
    private static Connection connection = null;
    
    /**
     * Get a database connection based on the configured database type
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                switch (DB_TYPE.toUpperCase()) {
                    case "H2":
                        Class.forName("org.h2.Driver");
                        connection = DriverManager.getConnection(H2_URL, H2_USER, H2_PASSWORD);
                        break;
                    
                    case "SQLITE":
                        Class.forName("org.sqlite.JDBC");
                        connection = DriverManager.getConnection(SQLITE_URL);
                        break;
                    
                    case "MYSQL":
                        Class.forName("com.mysql.cj.jdbc.Driver");
                        connection = DriverManager.getConnection(MYSQL_URL, MYSQL_USER, MYSQL_PASSWORD);
                        break;
                    
                    case "POSTGRESQL":
                        Class.forName("org.postgresql.Driver");
                        connection = DriverManager.getConnection(POSTGRESQL_URL, 
                                                                POSTGRESQL_USER, 
                                                                POSTGRESQL_PASSWORD);
                        break;
                    
                    default:
                        throw new SQLException("Unsupported database type: " + DB_TYPE);
                }
                
                System.out.println("Database connection established: " + DB_TYPE);
                
            } catch (ClassNotFoundException e) {
                throw new SQLException("Database driver not found: " + e.getMessage());
            }
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Database connection closed.");
            } catch (SQLException e) {
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
    
    /**
     * Initialize database tables (run once)
     */
    public static void initializeDatabase() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Drop existing tables
            stmt.execute("DROP TABLE IF EXISTS transactions");
            stmt.execute("DROP TABLE IF EXISTS accounts");
            stmt.execute("DROP TABLE IF EXISTS customers");
            
            // Create customers table
            stmt.execute("CREATE TABLE customers (" +
                        "customer_id VARCHAR(50) PRIMARY KEY, " +
                        "first_name VARCHAR(100) NOT NULL, " +
                        "last_name VARCHAR(100) NOT NULL, " +
                        "email VARCHAR(150) UNIQUE, " +
                        "phone VARCHAR(20), " +
                        "address VARCHAR(255), " +
                        "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP)");
            
            // Create accounts table
            stmt.execute("CREATE TABLE accounts (" +
                        "account_number VARCHAR(50) PRIMARY KEY, " +
                        "customer_id VARCHAR(50) NOT NULL, " +
                        "account_type VARCHAR(20) NOT NULL, " +
                        "balance DECIMAL(15, 2) DEFAULT 0.00, " +
                        "interest_rate DECIMAL(5, 4), " +
                        "withdrawal_limit DECIMAL(15, 2), " +
                        "overdraft_limit DECIMAL(15, 2), " +
                        "minimum_balance DECIMAL(15, 2), " +
                        "investment_type VARCHAR(50), " +
                        "term_months INT, " +
                        "created_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE)");
            
            // Create transactions table
            String transactionSQL = DB_TYPE.equals("SQLITE") ?
                "CREATE TABLE transactions (" +
                "transaction_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "account_number VARCHAR(50) NOT NULL, " +
                "transaction_type VARCHAR(20) NOT NULL, " +
                "amount DECIMAL(15, 2) NOT NULL, " +
                "balance_after DECIMAL(15, 2) NOT NULL, " +
                "description VARCHAR(255), " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "to_account_number VARCHAR(50), " +
                "FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE)" :
                
                "CREATE TABLE transactions (" +
                "transaction_id INT AUTO_INCREMENT PRIMARY KEY, " +
                "account_number VARCHAR(50) NOT NULL, " +
                "transaction_type VARCHAR(20) NOT NULL, " +
                "amount DECIMAL(15, 2) NOT NULL, " +
                "balance_after DECIMAL(15, 2) NOT NULL, " +
                "description VARCHAR(255), " +
                "transaction_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "to_account_number VARCHAR(50), " +
                "FOREIGN KEY (account_number) REFERENCES accounts(account_number) ON DELETE CASCADE)";
            
            stmt.execute(transactionSQL);
            
            System.out.println("Database tables initialized successfully!");
            
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Test database connection
     */
    public static void testConnection() {
        try {
            Connection conn = getConnection();
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection test: SUCCESS");
                System.out.println("Database: " + conn.getMetaData().getDatabaseProductName());
            }
        } catch (SQLException e) {
            System.err.println("Database connection test: FAILED");
            System.err.println("Error: " + e.getMessage());
        }
    }
}