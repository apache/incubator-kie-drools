package org.drools.verifier.report.components;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Object type that indicates an incompatibility between two objects.
 * 
 * Incompatibility happens when there is no value that would satisfy both objects.
 * 
 * 
 * Example: 
 * A: x > 10
 * B: x == 100
 * 
 * @author Toni Rikkola
 */
public class Incompatibility
    implements
    Cause {

    private final Cause  left;
    private final Cause  right;

    public Incompatibility(Cause left,
                           Cause right) {
        this.left = left;
        this.right = right;
    }

    public Cause getLeft() {
        return left;
    }

    public Cause getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + getLeft() + ") and (" + getRight() + ") are incompatible.";
    }

    public Collection<Cause> getCauses() {
        List<Cause> list = new ArrayList<Cause>();
        list.add( left );
        list.add( right );
        return list;
    }
}
