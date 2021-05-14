package org.drools.impact.analysis.model.right;

import java.util.ArrayList;
import java.util.List;

public class InsertAction extends ConsequenceAction {

    private final List<InsertedProperty> insertedProperties = new ArrayList<>();

    public InsertAction( Class<?> actionClass ) {
        super( Type.INSERT, actionClass );
    }

    public List<InsertedProperty> getInsertedProperties() {
        return insertedProperties;
    }

    public void addInsertedProperty(InsertedProperty insertedProperty) {
        insertedProperties.add( insertedProperty );
    }

    @Override
    public String toString() {
        return "InsertAction{" +
                "actionClass=" + actionClass +
                ", insertedProperties=" + insertedProperties +
                '}';
    }
}
