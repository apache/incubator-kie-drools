package org.drools.compiler.integrationtests.model;


public class RecordKey {
    private String receiptKey;
    private int lineNumber;

    public RecordKey(String receiptKey, int lineNumber) {
        this.receiptKey = receiptKey;
        this.lineNumber = lineNumber;
    }

    public String getReceiptKey() {
        return receiptKey;
    }

    public void setReceiptKey(String receiptKey) {
        this.receiptKey = receiptKey;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String toString() {
        return "RecordKey{" +
                "receiptKey=" + receiptKey +
                ", lineNumber='" + lineNumber + '\'' +
                '}';
    }
}
