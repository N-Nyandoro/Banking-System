import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Boundary Class - Account Management View
 * Handles account operations interface
 */
public class AccountView {
    private Stage stage;
    private AccountController controller;
    private CustomerClass currentCustomer;
    
    private ListView<String> accountListView;
    private Label balanceLabel;
    private Label accountDetailsLabel;
    private TextArea transactionHistoryArea;
    private TextField amountField;
    private ComboBox<String> accountComboBox;
    
    public AccountView(Stage stage, AccountController controller, CustomerClass customer) {
        this.stage = stage;
        this.controller = controller;
        this.currentCustomer = customer;
    }
    
    public Scene createScene() {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setStyle("-fx-background-color: #f5f5f5;");
        
        // Top bar
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);
        
        // Left sidebar - Account list
        VBox sidebar = createSidebar();
        mainLayout.setLeft(sidebar);
        
        // Center - Main content
        VBox centerContent = createCenterContent();
        mainLayout.setCenter(centerContent);
        
        // Right - Transaction panel
        VBox rightPanel = createRightPanel();
        mainLayout.setRight(rightPanel);
        
        return new Scene(mainLayout, 1200, 700);
    }
    
    private HBox createTopBar() {
        HBox topBar = new HBox(20);
        topBar.setPadding(new Insets(15, 20, 15, 20));
        topBar.setStyle("-fx-background-color: #2c3e50;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        
        Label titleLabel = new Label("Banking Dashboard");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setStyle("-fx-text-fill: white;");
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        Label userLabel = new Label("Welcome, " + currentCustomer.getFullName());
        userLabel.setFont(Font.font("Arial", 14));
        userLabel.setStyle("-fx-text-fill: white;");
        
        Button logoutButton = new Button("Logout");
        logoutButton.setStyle(
            "-fx-background-color: #e74c3c; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold;"
        );
        logoutButton.setOnAction(e -> controller.handleLogout());
        
        topBar.getChildren().addAll(titleLabel, spacer, userLabel, logoutButton);
        return topBar;
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(15);
        sidebar.setPadding(new Insets(20));
        sidebar.setPrefWidth(280);
        sidebar.setStyle("-fx-background-color: white;");
        
        Label accountsLabel = new Label("My Accounts");
        accountsLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        accountListView = new ListView<>();
        accountListView.setPrefHeight(300);
        accountListView.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldVal, newVal) -> {
                if (newVal != null) {
                    handleAccountSelection(newVal);
                }
            }
        );
        
        Button newAccountButton = new Button("+ Create New Account");
        newAccountButton.setPrefWidth(240);
        newAccountButton.setStyle(
            "-fx-background-color: #27ae60; " +
            "-fx-text-fill: white; " +
            "-fx-font-weight: bold;"
        );
        newAccountButton.setOnAction(e -> showCreateAccountDialog());
        
        Button refreshButton = new Button("🔄 Refresh");
        refreshButton.setPrefWidth(240);
        refreshButton.setOnAction(e -> refreshAccountList());
        
        sidebar.getChildren().addAll(
            accountsLabel, accountListView, 
            newAccountButton, refreshButton
        );
        
        return sidebar;
    }
    
    private VBox createCenterContent() {
        VBox centerContent = new VBox(20);
        centerContent.setPadding(new Insets(20));
        
        // Account details card
        VBox detailsCard = new VBox(15);
        detailsCard.setPadding(new Insets(25));
        detailsCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10;"
        );
        
        Label detailsTitle = new Label("Account Details");
        detailsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        
        accountDetailsLabel = new Label("Select an account to view details");
        accountDetailsLabel.setFont(Font.font("Arial", 14));
        accountDetailsLabel.setWrapText(true);
        
        balanceLabel = new Label("Balance: --");
        balanceLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        balanceLabel.setStyle("-fx-text-fill: #27ae60;");
        
        detailsCard.getChildren().addAll(
            detailsTitle, accountDetailsLabel, balanceLabel
        );
        
        // Transaction operations card
        VBox operationsCard = new VBox(15);
        operationsCard.setPadding(new Insets(25));
        operationsCard.setStyle(
            "-fx-background-color: white; " +
            "-fx-background-radius: 10;"
        );
        
        Label operationsTitle = new Label("Quick Operations");
        operationsTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        HBox amountBox = new HBox(10);
        amountField = new TextField();
        amountField.setPromptText("Enter amount");
        amountField.setPrefWidth(200);
        
        Button depositButton = new Button("Deposit");
        depositButton.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");
        depositButton.setOnAction(e -> handleDeposit());
        
        Button withdrawButton = new Button("Withdraw");
        withdrawButton.setStyle("-fx-background-color: #e67e22; -fx-text-fill: white;");
        withdrawButton.setOnAction(e -> handleWithdraw());
        
        Button transferButton = new Button("Transfer");
        transferButton.setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
        transferButton.setOnAction(e -> showTransferDialog());
        
        amountBox.getChildren().addAll(
            amountField, depositButton, withdrawButton, transferButton
        );
        
        operationsCard.getChildren().addAll(operationsTitle, amountBox);
        
        centerContent.getChildren().addAll(detailsCard, operationsCard);
        return centerContent;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(20));
        rightPanel.setPrefWidth(350);
        rightPanel.setStyle("-fx-background-color: white;");
        
        Label historyLabel = new Label("Transaction History");
        historyLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        
        transactionHistoryArea = new TextArea();
        transactionHistoryArea.setEditable(false);
        transactionHistoryArea.setPrefHeight(500);
        transactionHistoryArea.setStyle("-fx-font-family: 'Courier New'; -fx-font-size: 12px;");
        
        Button viewFullHistoryButton = new Button("View Full Statement");
        viewFullHistoryButton.setPrefWidth(310);
        viewFullHistoryButton.setOnAction(e -> showFullStatement());
        
        rightPanel.getChildren().addAll(
            historyLabel, transactionHistoryArea, viewFullHistoryButton
        );
        
        return rightPanel;
    }
    
    public void refreshAccountList() {
        ObservableList<String> accounts = FXCollections.observableArrayList();
        var accountsList = controller.getCustomerAccounts(currentCustomer.getCustomerId());
        
        for (AccountClass account : accountsList) {
            accounts.add(account.getAccountNumber() + " - " + 
                        account.getAccountType() + " - $" + 
                        String.format("%.2f", account.getBalance()));
        }
        
        accountListView.setItems(accounts);
    }
    
    private void handleAccountSelection(String selection) {
        String accountNumber = selection.split(" - ")[0];
        AccountClass account = controller.getAccountByNumber(accountNumber);
        
        if (account != null) {
            accountDetailsLabel.setText(
                "Account: " + account.getAccountNumber() + "\n" +
                "Type: " + account.getAccountType() + "\n" +
                "Customer: " + currentCustomer.getFullName()
            );
            
            balanceLabel.setText("Balance: $" + String.format("%.2f", account.getBalance()));
            
            // Load transaction history
            loadTransactionHistory(accountNumber);
        }
    }
    
    private void loadTransactionHistory(String accountNumber) {
        var transactions = controller.getRecentTransactions(accountNumber, 10);
        StringBuilder history = new StringBuilder();
        
        for (var tx : transactions) {
            history.append(String.format("%s\n%s: $%.2f\nBalance: $%.2f\n%s\n\n",
                tx.getTransactionDate(),
                tx.getTransactionType(),
                tx.getAmount(),
                tx.getBalanceAfter(),
                tx.getDescription() != null ? tx.getDescription() : ""
            ));
        }
        
        transactionHistoryArea.setText(history.toString());
    }
    
    private void handleDeposit() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an account first");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText());
            String accountNumber = selected.split(" - ")[0];
            
            if (controller.handleDeposit(accountNumber, amount)) {
                showAlert("Success", "Deposit successful!");
                refreshAccountList();
                handleAccountSelection(selected);
                amountField.clear();
            } else {
                showAlert("Error", "Deposit failed");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }
    
    private void handleWithdraw() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Error", "Please select an account first");
            return;
        }
        
        try {
            double amount = Double.parseDouble(amountField.getText());
            String accountNumber = selected.split(" - ")[0];
            
            if (controller.handleWithdraw(accountNumber, amount)) {
                showAlert("Success", "Withdrawal successful!");
                refreshAccountList();
                handleAccountSelection(selected);
                amountField.clear();
            } else {
                showAlert("Error", "Withdrawal failed - insufficient funds or exceeds limits");
            }
        } catch (NumberFormatException e) {
            showAlert("Error", "Please enter a valid amount");
        }
    }
    
    private void showTransferDialog() {
        // Implementation for transfer dialog
        showAlert("Info", "Transfer dialog - to be implemented");
    }
    
    private void showCreateAccountDialog() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create New Account");
        dialog.setHeaderText("Select account type:");
        
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        VBox content = new VBox(10);
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Savings", "Cheque", "Investment");
        typeCombo.setValue("Savings");
        
        TextField initialDepositField = new TextField("100");
        initialDepositField.setPromptText("Initial deposit");
        
        content.getChildren().addAll(
            new Label("Account Type:"), typeCombo,
            new Label("Initial Deposit:"), initialDepositField
        );
        
        dialog.getDialogPane().setContent(content);
        
        dialog.showAndWait().ifPresent(result -> {
            try {
                double initialDeposit = Double.parseDouble(initialDepositField.getText());
                String type = typeCombo.getValue();
                
                if (controller.handleCreateAccount(currentCustomer.getCustomerId(), 
                                                   type, initialDeposit)) {
                    showAlert("Success", "Account created successfully!");
                    refreshAccountList();
                }
            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid deposit amount");
            }
        });
    }
    
    private void showFullStatement() {
        String selected = accountListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            String accountNumber = selected.split(" - ")[0];
            controller.showAccountStatement(accountNumber);
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}