package org.kie.internal.builder;

import java.util.ArrayList;
import java.util.List;

public class ResourceChangeSet {
    private final String resourceName;  // src/main/resources/org/drools/rules.drl
    private final ChangeType status;
    private final List<ResourceChange> changes = new ArrayList<ResourceChange>();
    
    public ResourceChangeSet(String resourceName, ChangeType status) {
        this.resourceName = resourceName;
        this.status = status;
    }
    
    public String getResourceName() {
        return resourceName;
    }
    
    public ChangeType getChangeType() {
        return status;
    }
    
    public List<ResourceChange> getChanges() {
        return changes;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((changes == null) ? 0 : changes.hashCode());
        result = prime * result + ((resourceName == null) ? 0 : resourceName.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if ( this == obj ) return true;
        if ( obj == null ) return false;
        if ( getClass() != obj.getClass() ) return false;
        ResourceChangeSet other = (ResourceChangeSet) obj;
        if ( changes == null ) {
            if ( other.changes != null ) return false;
        } else if ( !changes.equals( other.changes ) ) return false;
        if ( resourceName == null ) {
            if ( other.resourceName != null ) return false;
        } else if ( !resourceName.equals( other.resourceName ) ) return false;
        if ( status != other.status ) return false;
        return true;
    }
}
