package org.drools.rule;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public abstract class GroupElement extends ConditionalElement {
    private List children = new ArrayList();

    public void addChild(Object child) {
        this.children.add( child );
    }

    public List getChildren() {
        return this.children;
    }

    /**
     * Traverses two trees and checks that they are structurally equal at all
     * levels
     * 
     * @param e1
     * @param e2
     * @return
     */
    public boolean equals(Object object) {
        // Return false if its null or not an instance of ConditionalElement
        if ( object == null || !(object instanceof GroupElement) ) {
            return false;
        }

        // Return true if they are the same reference
        if ( this == object ) {
            return true;
        }

        // Now try a recurse manual check
        GroupElement e2 = (GroupElement) object;
        List e1Children = this.getChildren();
        List e2Children = e2.getChildren();
        if ( e1Children.size() != e2Children.size() ) {
            return false;
        }

        for ( int i = 0; i < e1Children.size(); i++ ) {
            Object e1Object1 = e1Children.get( i );
            Object e2Object1 = e2Children.get( i );
            if ( e1Object1 instanceof GroupElement ) {
                if ( e1Object1.getClass().isInstance( e2Object1 ) ) {
                    if ( !e1Object1.equals( e2Object1 ) ) {
                        System.out.println( e1Object1.getClass().getName() + " did not have identical children" );
                        return false;
                    }
                } else {
                    System.out.println( "Should be the equal Conditionalelements but instead was '" + e1Object1.getClass().getName() + "', '" + e2Object1.getClass().getName() + "'" );
                    return false;
                }
            } else if ( e1Object1 instanceof String ) {
                if ( !e1Object1.equals( e2Object1 ) ) {
                    System.out.println( "Should be the equal Strings but instead was '" + e1Object1 + "', '" + e2Object1 + "'" );
                    return false;
                }
            } else {
                System.out.println( "Objects are neither instances of ConditionalElement or String" );
                return false;
            }
        }

        return true;
    }

    public int hashCode() {
        return this.children.hashCode();
    }

    /**
     * Clones all Conditional Elements but references the non ConditionalElement
     * children
     * 
     * @param e1
     * @param e2
     * @return
     */
    public Object clone() {
        GroupElement cloned = null;

        try {
            cloned = (GroupElement) this.getClass().newInstance();
        } catch ( InstantiationException e ) {
            throw new RuntimeException( "Could not clone '" + this.getClass().getName() + "'" );
        } catch ( IllegalAccessException e ) {
            throw new RuntimeException( "Could not clone '" + this.getClass().getName() + "'" );
        }

        for ( Iterator it = this.children.iterator(); it.hasNext(); ) {
            Object object = it.next();
            if ( object instanceof GroupElement ) {
                object = ((GroupElement) object).clone();
            }
            cloned.addChild( object );

        }

        return cloned;
    }

}
