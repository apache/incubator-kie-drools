package org.drools.core.common;

import org.drools.core.WorkingMemoryEntryPoint;
import org.kie.api.runtime.rule.SessionEntryPoint;

public class TraitFactHandle extends DefaultFactHandle implements InternalFactHandle {

    private boolean trait;

    public TraitFactHandle( int id, Object object, long recency, SessionEntryPoint wmEntryPoint, boolean isTrait ) {
        super( id, object, recency, wmEntryPoint );
        this.trait = isTrait;
    }

    public boolean isTraitable() {
        return ! trait;
    }

    public boolean isTraiting() {
        return trait;
    }

    @Override
    public boolean isTrait() {
        return true;
    }
}