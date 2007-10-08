package org.drools.tutorials.banking;

import java.util.Date;

public class AllocatedCashflow extends TypedCashflow {
    private Account account;

    public AllocatedCashflow() {
    }

    public AllocatedCashflow(Account account,
                             Date date,
                             int type,
                             double amount) {
        super( date,
               type,
               amount );
        this.account = account;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String toString() {
        return "AllocatedCashflow[" + "account=" + account + ",date=" + getDate() + 
                                  ",type=" + (getType() == CREDIT ? "Credit" : "Debit") + 
                                  ",amount=" + getAmount() + "]";
    }
}