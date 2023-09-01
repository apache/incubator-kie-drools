package org.drools.examples.cashflow;


import java.util.Date;

public class CashFlow {
    private Date date;
    private int amount;
    private CashFlowType type;
    private int accountNo;

    public CashFlow(Date date, int amount, CashFlowType type,int accountNo) {
        this.date = date;
        this.type = type;
        this.amount = amount;
        this.accountNo = accountNo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public CashFlowType getType() {
        return type;
    }

    public void setType(CashFlowType type) {
        this.type = type;
    }

    public int getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(int accountNo) {
        this.accountNo = accountNo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }

        CashFlow cashFlow = (CashFlow) o;

        if (type != cashFlow.type) { return false; }
        if (accountNo != cashFlow.accountNo) { return false; }
        if (amount != cashFlow.amount) { return false; }
        if (!date.equals(cashFlow.date)) { return false; }

        return true;
    }

    @Override
    public int hashCode() {
        int result = date.hashCode();
        result = 31 * result + type.hashCode();
        result = 31 * result + amount;
        result = 31 * result + accountNo;
        return result;
    }

    @Override
    public String toString() {
        return "CashFlow{" +
               "date=" + date +
               ", amount=" + amount +
               ", type=" + type +
               ", accountNo=" + accountNo +
               '}';
    }
}
