/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
