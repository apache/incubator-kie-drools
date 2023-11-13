package org.drools.compiler.integrationtests.model;

import java.util.List;

public class CalcFact {

    private List<Item> itemList;
    private int lineNumber;

    public CalcFact(List<Item> itemList, int lineNumber) {
        this.itemList = itemList;
        this.lineNumber = lineNumber;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public String toString() {
        return "CalcFact{" +
                "itemList=" + itemList +
                ", lineNumber=" + lineNumber +
                '}';
    }
}
