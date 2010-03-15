package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.verifier.data.VerifierComponent;

/**
 * Object type that indicates an overlap between two objects.
 * 
 * Overlap happens when only some values can satisfy both objects
 * 
 * Example "values between 1 and 9 can satisfy both":
 * A: x > 10 
 * B: x < 0 
 * 
 * @author toni rikkola
 */
public class Overlap
    implements
    Cause {

    private final List<Cause> items = new ArrayList<Cause>( 2 );

    public Overlap(VerifierComponent first,
                   VerifierComponent second) {
        items.add( first );
        items.add( second );
    }

    public List<Cause> getItems() {
        return items;
    }

    public Collection<Cause> getCauses() {
        return items;
    }

    @Override
    public String toString() {
        return "Overlap between: (" + items.get( 0 ) + ") and (" + items.get( 1 ) + ").";
    }
}
