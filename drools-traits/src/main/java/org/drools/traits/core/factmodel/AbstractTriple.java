package org.drools.traits.core.factmodel;

import org.kie.api.runtime.rule.Variable;

public abstract class AbstractTriple implements Triple {

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + getInstance().hashCode();
        result = prime * result + getProperty().hashCode();
        return result;
    }

    @Override
    public boolean equals(Object object) {
        return equals(this, object);
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == null || object2 == null ) {
            return object1 == object2;
        }

        Triple t1 = (Triple) object1;
        Triple t2 = (Triple) object2;

        if (t1.getInstance() != Variable.v) {
            if (t1.getInstance() == null) {
                return false;
            } else if (t1.getInstance() instanceof String) {
                if (!t1.getInstance().equals(t2.getInstance())) {
                    return false;
                }
            } else if (t1.getInstance() != t2.getInstance()) {
                return false;
            }
        }

        if (t1.getProperty() != Variable.v && !t1.getProperty().equals(t2.getProperty())) {
            return false;
        }
        if (t1.getValue() != Variable.v) {
            if (t1.getValue() == null) {
                return t2.getValue() == null;
            } else {
                return t1.getValue().equals(t2.getValue());
            }
        }

        if (t1.getClass() == TripleStore.TripleCollector.class) {
            ((TripleStore.TripleCollector)t1).list.add(t2);
            return false;
        }

        return true;
    }
}
