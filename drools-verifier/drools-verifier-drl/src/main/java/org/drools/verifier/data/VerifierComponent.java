package org.drools.verifier.data;

import java.util.Collection;
import java.util.Collections;

import org.drools.drl.ast.descr.BaseDescr;
import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;

/** 
 */
public abstract class VerifierComponent<D extends BaseDescr>
    implements
    Comparable<VerifierComponent>,
    Cause {

    private D descr;
    
    public VerifierComponent(D descr) {
        this.descr = descr;
    }
    
    public abstract String getPath();

    public abstract VerifierComponentType getVerifierComponentType();

    public int compareTo(VerifierComponent another) {
        return this.getPath().compareTo( another.getPath() );
    }

    public Collection<Cause> getCauses() {
        return Collections.emptyList();
    }

    @Override
    public String toString() {
        return this.getClass().getName() + " " + getPath();
    }

    public D getDescr() {
        return descr;
    }
}
