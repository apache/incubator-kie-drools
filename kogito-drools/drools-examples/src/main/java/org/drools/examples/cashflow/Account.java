package org.drools.examples.cashflow;

public class Account {
    private int accountNo;
    private int balance;

    public Account(int accountNo, int balance) {
        this.accountNo = accountNo;
        this.balance = balance;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        Account account = (Account) o;

        if (accountNo != account.accountNo) { return false; }
        if (balance != account.balance) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = accountNo;
        result = 31 * result + balance;
        return result;
    }

    @Override
    public String toString() {
        return "Account{" +
               "accountNo=" + accountNo +
               ", balance=" + balance +
               '}';
    }
}
