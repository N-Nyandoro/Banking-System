public interface WithdrawableInterface {
    boolean withdraw(double amount);
    double getWithdrawalLimit();
    void setWithdrawalLimit(double limit);
}