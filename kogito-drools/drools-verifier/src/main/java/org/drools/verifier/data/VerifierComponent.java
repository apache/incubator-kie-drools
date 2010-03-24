package org.drools.verifier.data;

import java.util.Collection;
import java.util.Collections;

import org.drools.verifier.components.VerifierComponentType;
import org.drools.verifier.report.components.Cause;

/** 
 * 
 * @author Toni Rikkola
 */
public abstract class VerifierComponent
    implements
    Comparable<VerifierComponent>,
    Cause {

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
}
