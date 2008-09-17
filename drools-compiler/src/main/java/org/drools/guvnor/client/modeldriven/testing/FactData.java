package org.drools.guvnor.client.modeldriven.testing;

import java.util.ArrayList;
import java.util.List;

public class FactData implements Fixture {

    /**
     * The type (class)
     */
    public String type;

    /**
     * The name of the "variable"
     */
    public String name;

    public List<FieldData> fieldData = new ArrayList<FieldData>();

    /**
     * If its a modify, obviously we are modifying existing data in working memory.
     */
    public boolean isModify;

    public FactData() {}
    public FactData(String type, String name, List<FieldData> fieldData, boolean modify) {

        this.type = type;
        this.name = name;
        this.fieldData = fieldData;
        this.isModify = modify;

    }




}
