package org.drools.model;

import java.util.Collections;
import java.util.List;

public interface Condition {

    default List<Condition> getSubConditions() {
        return Collections.emptyList();
    }

    Type getType();

    Variable<?>[] getBoundVariables();

    enum Type {
        PATTERN( false ), QUERY( false ), ACCUMULATE( false ), TEMPORAL( false ), OOPATH( false ),
        OR( true ), AND( true ), NOT( false ), EXISTS( false ), FORALL( false ), CONSEQUENCE( false );

        private final boolean composite;

        Type( boolean composite ) {
            this.composite = composite;
        }

        public boolean isComposite() {
            return composite;
        }
    }
}
