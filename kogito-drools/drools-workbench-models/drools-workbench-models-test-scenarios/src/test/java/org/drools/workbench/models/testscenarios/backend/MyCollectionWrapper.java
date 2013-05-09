package org.drools.workbench.models.testscenarios.backend;

import java.util.ArrayList;

public class MyCollectionWrapper {

    private ArrayList<?> list = new ArrayList<Object>();

    public ArrayList<?> getList() {
        return list;
    }

    public void setList(ArrayList<?> list) {
        this.list = list;
    }
}