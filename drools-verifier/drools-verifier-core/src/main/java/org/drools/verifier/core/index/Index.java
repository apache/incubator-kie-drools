package org.drools.verifier.core.index;

import org.drools.verifier.core.index.model.Columns;
import org.drools.verifier.core.index.model.ObjectTypes;
import org.drools.verifier.core.index.model.Rules;

public interface Index {

    Rules getRules();

    Columns getColumns();

    ObjectTypes getObjectTypes();
}
