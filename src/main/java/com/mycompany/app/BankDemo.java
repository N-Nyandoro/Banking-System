public class BankSystemDemo {
    public static void main(String[] args) {
        // Create the bank system
        BankSystemClass bank = new BankSystemClass("FirstBank");
        System.out.println("=== " + bank.getBankName() + " Banking System ===\n");
        
        // Create customers
        System.out.println("Creating customers...");
        CustomerClass customer1 = bank.createCustomer("John", "Doe", 
                                                      "john.doe@email.com", 
                                                      "555-0101", 
                                                      "123 Main St");
        CustomerClass customer2 = bank.createCustomer("Jane", "Smith", 
                                                      "jane.smith@email.com", 
                                                      "555-0102", 
                                                      "456 Oak Ave");
        System.out.println("Created: " + customer1);
        System.out.println("Created: " + customer2);
        System.out.println();
        
        // Create accounts
        System.out.println("Creating accounts...");
        ChequeAccountClass chequeAcc1 = bank.createChequeAccount(customer1.getCustomerId());
        SavingsAccountClass savingsAcc1 = bank.createSavingsAccount(customer1.getCustomerId(), 500.0);
        InvestmentAccountClass investAcc1 = bank.createInvestmentAccount(customer1.getCustomerId(), 
                                                                         "Fixed Deposit", 
                                                                         5000.0);
        
        ChequeAccountClass chequeAcc2 = bank.createChequeAccount(customer2.getCustomerId());
        SavingsAccountClass savingsAcc2 = bank.createSavingsAccount(customer2.getCustomerId(), 1000.0);
        
        System.out.println("Created accounts for " + customer1.getFullName());
        System.out.println("Created accounts for " + customer2.getFullName());
        System.out.println();
        
        // Perform transactions
        System.out.println("=== Performing Transactions ===");
        
        // Deposits
        System.out.println("\n1. Depositing funds...");
        bank.deposit(chequeAcc1.getAccountNumber(), 2000.0);
        System.out.println("Deposited 2000.00 to " + chequeAcc1.getAccountNumber());
        System.out.println("Balance: " + chequeAcc1.getBalance());
        
        bank.deposit(chequeAcc2.getAccountNumber(), 1500.0);
        System.out.println("Deposited 1500.00 to " + chequeAcc2.getAccountNumber());
        System.out.println("Balance: " + chequeAcc2.getBalance());
        
        // Withdrawals
        System.out.println("\n2. Withdrawing funds...");
        if (bank.withdraw(chequeAcc1.getAccountNumber(), 500.0)) {
            System.out.println("Successfully withdrew 500.00 from " + chequeAcc1.getAccountNumber());
            System.out.println("New balance: " + chequeAcc1.getBalance());
        }
        
        // Test withdrawal limit
        System.out.println("\n3. Testing withdrawal limits...");
        if (!bank.withdraw(savingsAcc1.getAccountNumber(), 450.0)) {
            System.out.println("Withdrawal of 450.00 from " + savingsAcc1.getAccountNumber() + 
                             " failed (minimum balance requirement)");
            System.out.println("Current balance: " + savingsAcc1.getBalance());
        }
        
        // Successful savings withdrawal
        if (bank.withdraw(savingsAcc2.getAccountNumber(), 500.0)) {
            System.out.println("Successfully withdrew 500.00 from " + savingsAcc2.getAccountNumber());
            System.out.println("New balance: " + savingsAcc2.getBalance());
        }
        
        // Transfer
        System.out.println("\n4. Performing transfer...");
        if (bank.transfer(chequeAcc1.getAccountNumber(), chequeAcc2.getAccountNumber(), 300.0)) {
            System.out.println("Successfully transferred 300.00");
            System.out.println("From " + chequeAcc1.getAccountNumber() + " (Balance: " + 
                             chequeAcc1.getBalance() + ")");
            System.out.println("To " + chequeAcc2.getAccountNumber() + " (Balance: " + 
                             chequeAcc2.getBalance() + ")");
        }
        
        // Calculate Interest
        System.out.println("\n5. Calculating interest for all accounts...");
        bank.calculateInterestForAllAccounts();
        System.out.println("Interest calculated.");
        System.out.println("Savings Account 1 balance: " + savingsAcc1.getBalance());
        System.out.println("Savings Account 2 balance: " + savingsAcc2.getBalance());
        System.out.println("Investment Account balance: " + investAcc1.getBalance());
        
        // Display customer accounts
        System.out.println("\n=== Customer Account Summary ===");
        System.out.println("\n" + customer1.getFullName() + "'s Accounts:");
        for (AccountClass acc : customer1.getAccounts()) {
            System.out.println("  " + acc);
        }
        System.out.println("Total Balance: " + String.format("%.2f", customer1.getTotalBalance()));
        
        System.out.println("\n" + customer2.getFullName() + "'s Accounts:");
        for (AccountClass acc : customer2.getAccounts()) {
            System.out.println("  " + acc);
        }
        System.out.println("Total Balance: " + String.format("%.2f", customer2.getTotalBalance()));
        
        // Bank summary
        System.out.println("\n=== Bank Summary ===");
        System.out.println(bank);
    }
}