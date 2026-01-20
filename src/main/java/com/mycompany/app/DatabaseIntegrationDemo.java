public class DatabaseIntegrationDemo {
    
    public static void main(String[] args) {
        System.out.println("=================================");
        System.out.println("Banking System - Database Demo");
        System.out.println("=================================\n");
        
        // Step 1: Initialize Database
        System.out.println("1. INITIALIZING DATABASE...");
        DatabaseConnection.testConnection();
        DatabaseConnection.initializeDatabase();
        System.out.println();
        
        // Step 2: Create Bank System
        System.out.println("2. CREATING BANK SYSTEM...");
        BankSystemWithDAO bank = new BankSystemWithDAO("FirstBank Digital");
        System.out.println("Bank created: " + bank.getBankName());
        System.out.println();
        
        // Step 3: Create Customers
        System.out.println("3. CREATING CUSTOMERS...");
        CustomerClass customer1 = bank.createCustomer(
            "John", "Doe", 
            "john.doe@email.com", 
            "555-0101", 
            "123 Main Street, Cityville"
        );
        
        CustomerClass customer2 = bank.createCustomer(
            "Jane", "Smith", 
            "jane.smith@email.com", 
            "555-0102", 
            "456 Oak Avenue, Townsburg"
        );
        
        CustomerClass customer3 = bank.createCustomer(
            "Bob", "Johnson", 
            "bob.johnson@email.com", 
            "555-0103", 
            "789 Pine Road, Villageton"
        );
        
        System.out.println("Total customers created: " + bank.getTotalCustomers());
        System.out.println();
        
        // Step 4: Create Various Accounts
        System.out.println("4. CREATING ACCOUNTS...");
        
        // Customer 1 accounts
        ChequeAccountClass cheque1 = bank.createChequeAccount(customer1.getCustomerId());
        SavingsAccountClass savings1 = bank.createSavingsAccount(customer1.getCustomerId(), 500.0);
        InvestmentAccountClass invest1 = bank.createInvestmentAccount(
            customer1.getCustomerId(), "Fixed Deposit", 5000.0
        );
        
        // Customer 2 accounts
        ChequeAccountClass cheque2 = bank.createChequeAccount(customer2.getCustomerId());
        SavingsAccountClass savings2 = bank.createSavingsAccount(customer2.getCustomerId(), 1000.0);
        
        // Customer 3 accounts
        ChequeAccountClass cheque3 = bank.createChequeAccount(customer3.getCustomerId());
        
        System.out.println("Total accounts created: " + bank.getTotalAccounts());
        System.out.println();
        
        // Step 5: Perform Transactions
        System.out.println("5. PERFORMING TRANSACTIONS...");
        
        System.out.println("\n5.1 Deposits:");
        bank.deposit(cheque1.getAccountNumber(), 2000.0);
        bank.deposit(cheque2.getAccountNumber(), 1500.0);
        bank.deposit(cheque3.getAccountNumber(), 3000.0);
        
        System.out.println("\n5.2 Withdrawals:");
        bank.withdraw(cheque1.getAccountNumber(), 500.0);
        bank.withdraw(savings2.getAccountNumber(), 300.0);
        
        System.out.println("\n5.3 Transfers:");
        bank.transfer(cheque1.getAccountNumber(), cheque2.getAccountNumber(), 200.0);
        bank.transfer(cheque3.getAccountNumber(), savings2.getAccountNumber(), 500.0);
        
        System.out.println("\n5.4 Interest Calculation:");
        bank.calculateInterestForAllAccounts();
        System.out.println();
        
        // Step 6: Display Account Statements
        System.out.println("6. ACCOUNT STATEMENTS:");
        bank.printAccountStatement(cheque1.getAccountNumber());
        bank.printAccountStatement(savings1.getAccountNumber());
        bank.printAccountStatement(invest1.getAccountNumber());
        
        // Step 7: Customer Information Retrieval
        System.out.println("7. RETRIEVING CUSTOMER INFORMATION...");
        CustomerClass retrievedCustomer = bank.getCustomerWithAccounts(customer1.getCustomerId());
        if (retrievedCustomer != null) {
            System.out.println("\nCustomer: " + retrievedCustomer.getFullName());
            System.out.println("Email: " + retrievedCustomer.getEmail());
            System.out.println("Phone: " + retrievedCustomer.getPhone());
            System.out.println("\nAccounts:");
            for (AccountClass account : retrievedCustomer.getAccounts()) {
                System.out.println("  " + account);
            }
            System.out.println("Total Balance: " + 
                             String.format("%.2f", retrievedCustomer.getTotalBalance()));
        }
        System.out.println();
        
        // Step 8: DAO Direct Usage Examples
        System.out.println("8. DIRECT DAO OPERATIONS...");
        
        // Using CustomerDAO
        CustomerDAO customerDAO = new CustomerDAO();
        System.out.println("\n8.1 Search customers by name:");
        var searchResults = customerDAO.searchCustomersByName("John");
        for (CustomerClass c : searchResults) {
            System.out.println("  Found: " + c.getFullName() + " (" + c.getEmail() + ")");
        }
        
        // Using AccountDAO
        AccountDAO accountDAO = new AccountDAO();
        System.out.println("\n8.2 Get all savings accounts:");
        var savingsAccounts = accountDAO.getAccountsByType("Savings");
        for (AccountClass acc : savingsAccounts) {
            System.out.println("  " + acc);
        }
        
        // Using TransactionDAO
        TransactionDAO transactionDAO = new TransactionDAO();
        System.out.println("\n8.3 Transaction statistics for " + cheque1.getAccountNumber() + ":");
        int txCount = transactionDAO.getTransactionCount(cheque1.getAccountNumber());
        double totalDeposits = transactionDAO.getTotalDeposits(cheque1.getAccountNumber());
        double totalWithdrawals = transactionDAO.getTotalWithdrawals(cheque1.getAccountNumber());
        System.out.println("  Total Transactions: " + txCount);
        System.out.println("  Total Deposits: " + String.format("%.2f", totalDeposits));
        System.out.println("  Total Withdrawals: " + String.format("%.2f", totalWithdrawals));
        
        // Step 9: Update Operations
        System.out.println("\n9. UPDATE OPERATIONS...");
        customer1.setPhone("555-9999");
        customer1.setAddress("999 New Street, Newcity");
        if (bank.updateCustomer(customer1)) {
            System.out.println("Customer information updated successfully");
        }
        
        // Step 10: Bank Summary Report
        System.out.println("\n10. BANK SUMMARY REPORT:");
        System.out.println("================================");
        System.out.println(bank);
        System.out.println("================================");
        
        // Step 11: Test Data Persistence
        System.out.println("\n11. TESTING DATA PERSISTENCE...");
        System.out.println("All data has been persisted to the database.");
        System.out.println("You can restart the application and the data will be available.");
        System.out.println("Database file location: ./bankdb.mv.db (H2) or bankdb.db (SQLite)");
        
        System.out.println("\n=================================");
        System.out.println("Demo completed successfully!");
        System.out.println("=================================");
    }
}
