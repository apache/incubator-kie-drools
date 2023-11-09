package org.drools.compiler.integrationtests.model;

import java.util.List;

public class CalcFact {

    private String receiptKey;
    private List<Item> itemList;
    private PointKey pointKey;

    public String getReceiptKey() {
        return receiptKey;
    }

    public void setReceiptKey(String receiptKey) {
        this.receiptKey = receiptKey;
    }

    public CalcFact(String receiptKey, List<Item> itemList, PointKey pointKey) {
        this.receiptKey = receiptKey;
        this.itemList = itemList;
        this.pointKey = pointKey;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public PointKey getPointKey() {
        return pointKey;
    }

    public void setPointKey(PointKey pointKey) {
        this.pointKey = pointKey;
    }

    @Override
    public String toString() {
        return "CalcFact{" +
                "receiptKey='" + receiptKey + '\'' +
                ", itemList=" + itemList +
                ", pointKey=" + pointKey +
                '}';
    }
}
