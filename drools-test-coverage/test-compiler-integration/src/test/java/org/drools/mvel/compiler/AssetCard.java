package org.drools.mvel.compiler;

public class AssetCard {

    private int number;

    private Asset parent;

    private String groupCode;

    public AssetCard(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public Asset getParent() {
        return parent;
    }

    public void setParent(Asset parent) {
        this.parent = parent;
    }

    public String getGroupCode() {
        return groupCode;
    }

    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
    }

    @Override
    public String toString() {
        return "AssetCard{" +
               "number=" + number +
               '}';
    }
}
