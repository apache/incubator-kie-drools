package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.List;

/**
 * Presents a redundancy between two Causes. The link between them can be WEAK
 * or STRONG.
 * 
 * WEAK redundancy is for example two VerifierRules, but not their rule
 * possibilities. STRONG redundancy includes possibilities.
 * 
 * @author Toni Rikkola
 */
public class Redundancy
    implements
    Cause {

    private static int        index = 0;

    private final String      guid  = String.valueOf( index++ );

    private final List<Cause> items = new ArrayList<Cause>( 2 );

    public Redundancy(Cause first,
                      Cause second) {
        items.add( first );
        items.add( second );
    }

    public String getGuid() {
        return guid;
    }

    public CauseType getCauseType() {
        return CauseType.REDUNDANCY;
    }

    public List<Cause> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Redundancy between: (" + items.get( 0 ) + ") and (" + items.get( 1 ) + ").";
    }
}
