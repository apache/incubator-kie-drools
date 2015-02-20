package org.drools.compiler.kie.util;

import org.kie.internal.builder.ResourceChangeSet;

import java.util.HashMap;
import java.util.Map;

public class KieJarChangeSet {
    private final Map<String, ResourceChangeSet> changes = new HashMap<String, ResourceChangeSet>();

    public Map<String, ResourceChangeSet> getChanges() {
        return changes;
    }

    public boolean contains(String resourceName) {
        return changes.keySet().contains(resourceName);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changes == null) ? 0 : changes.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        KieJarChangeSet other = (KieJarChangeSet) obj;
        if ( changes == null ) {
            if ( other.changes != null ) return false;
        } else if ( !changes.equals( other.changes ) ) return false;
        return true;
    }

    @Override
    public String toString() {
        return "KieJarChangeSet [changes=" + changes + "]";
    }
}
