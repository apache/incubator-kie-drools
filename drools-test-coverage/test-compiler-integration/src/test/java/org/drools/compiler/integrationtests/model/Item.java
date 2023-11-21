package org.drools.compiler.integrationtests.model;

public class Item {

    private String decomposedPointFlag;

    public Item(String decomposedPointFlag) {
        this.decomposedPointFlag = decomposedPointFlag;
    }

    public String getDecomposedPointFlag() {
        return decomposedPointFlag;
    }

    public void setDecomposedPointFlag(String decomposedPointFlag) {
        this.decomposedPointFlag = decomposedPointFlag;
    }

    @Override
    public String toString() {
        return "Item{" +
                "pointFlag='" + decomposedPointFlag + '\'' +
                '}';
    }
}
