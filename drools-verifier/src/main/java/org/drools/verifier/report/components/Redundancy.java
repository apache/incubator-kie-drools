package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.verifier.data.VerifierComponent;

/**
 * Object type that indicates a redundancy between two objects.
 * 
 * Redundancy happens when all the possible values satisfy both objects.
 * 
 * Example:
 * A: x == 10
 * B: x == 10
 * 
 * @author Toni Rikkola
 */
public class Redundancy
    implements
    Cause {

    private final List<VerifierComponent> items = new ArrayList<VerifierComponent>( 2 );

    private final Collection<Cause>       causes;

    public Redundancy(VerifierComponent first,
                      VerifierComponent second) {
        items.add( first );
        items.add( second );
        this.causes = Collections.emptyList();
    }

    public Redundancy(VerifierComponent first,
                      VerifierComponent second,
                      Collection<Cause> causes) {
        items.add( first );
        items.add( second );
        this.causes = causes;
    }

    public List<VerifierComponent> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Redundancy between: (" + items.get( 0 ) + ") and (" + items.get( 1 ) + ").";
    }

    public Collection<Cause> getCauses() {
        return causes;
    }
}
