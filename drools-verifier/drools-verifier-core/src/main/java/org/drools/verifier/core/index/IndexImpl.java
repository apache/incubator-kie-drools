package org.drools.verifier.core.index;

import org.drools.verifier.core.index.model.Columns;
import org.drools.verifier.core.index.model.ObjectTypes;
import org.drools.verifier.core.index.model.Rules;

public class IndexImpl
        implements Index {

    private Rules rules = new Rules();

    private Columns columns = new Columns();

    private ObjectTypes objectTypes = new ObjectTypes();

    @Override
    public Rules getRules() {
        return rules;
    }

    @Override
    public Columns getColumns() {
        return columns;
    }

    @Override
    public ObjectTypes getObjectTypes() {
        return objectTypes;
    }
}
