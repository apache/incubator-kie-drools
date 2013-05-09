package org.drools.workbench.models.testscenarios.shared;

import java.util.ArrayList;
import java.util.List;

public class CollectionFieldData implements Field {

    private String name;

    private List<FieldData> collectionFieldList = new ArrayList<FieldData>();

    @Override
    public String getName() {
        return name;
    }

    public void setName( final String name ) {
        this.name = name;
    }

    public List<FieldData> getCollectionFieldList() {
        return collectionFieldList;
    }

    public void setCollectionFieldList( final List<FieldData> collectionFieldList ) {
        this.collectionFieldList = collectionFieldList;
    }
}
