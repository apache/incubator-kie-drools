package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.drools.verifier.data.VerifierComponent;

/**
 * Object type that indicates a equivalence between two objects.
 * 
 * Equivalence happens when the LHS of the rules are redundant, but the LHS is different.
 * 
 * @author Toni Rikkola
 */
public class Equivalence
    implements
    Cause {

    private final List<VerifierComponent> items = new ArrayList<VerifierComponent>( 2 );
    private final Collection<Cause>       causes;

    public Equivalence(VerifierComponent first,
                       VerifierComponent second) {
        items.add( first );
        items.add( second );
        this.causes = Collections.emptyList();
    }

    public Equivalence(VerifierComponent first,
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
        return "Equivalency between: (" + items.get( 0 ) + ") and (" + items.get( 1 ) + ").";
    }

    public Collection<Cause> getCauses() {
        return causes;
    }
}
