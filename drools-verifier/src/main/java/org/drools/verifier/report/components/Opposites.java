package org.drools.verifier.report.components;

/**
 * Object type that indicates an opposity between two objects.
 * 
 * 
 * Opposity happens when only the values that would satisfy object A 
 * can not satisfy object B.
 * 
 * Example: 
 * A: a == 10
 * B: a != 10
 * 
 * @author Toni Rikkola
 */
public class Opposites extends Incompatibility
    implements
    Cause {

    public Opposites(Cause left,
                     Cause right) {
        super( left,
               right );
    }

    @Override
    public String toString() {
        return "Opposites: (" + getLeft() + ") and (" + getRight() + ").";
    }
}
