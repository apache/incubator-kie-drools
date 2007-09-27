package org.drools.brms.client.modeldriven.brl;

/**
 * This is a field constraint that may span multiple fields.
 *
 * @author Michael Neale
 */
public class CompositeFieldConstraint implements FieldConstraint {


    /**
     * Means that any of the children can resolve to be true.
     */
    public static final String COMPOSITE_TYPE_OR = "||";

    /**
     * Means that ALL of the children constraints must resolve to be true.
     */
    public static final String COMPOSITE_TYPE_AND = "&&";


    /**
     * The type of composite that it is.
     */
    public String compositeJunctionType = null;


    /**
     * This is the child field constraints of the composite.
     * They may be single constraints, or composite themselves.
     * If this composite is it at the "top level"  - then
     * there is no need to look at the compositeType property
     * (as they are all children that are "anded" together anyway in the fact
     * pattern that contains it).
     */
    public FieldConstraint[] constraints = null;

    //Note this is a bit ugly, GWT had some early limitations which required this to kind of work this way.
    //when generics are available, could probably switch to it, but remember this is persistent stuff
    //so don't want to break backwards compat (as XStream is used)
    public void addConstraint(final FieldConstraint constraint) {
        if ( this.constraints == null ) {
            this.constraints = new FieldConstraint[1];
            this.constraints[0] = constraint;
        } else {
            final FieldConstraint[] newList = new FieldConstraint[this.constraints.length + 1];
            for ( int i = 0; i < this.constraints.length; i++ ) {
                newList[i] = this.constraints[i];
            }
            newList[this.constraints.length] = constraint;
            this.constraints = newList;
        }
    }

    public void removeConstraint(final int idx) {
        //Unfortunately, this is kinda duplicate code with other methods,
        //but with typed arrays, and GWT, its not really possible to do anything "better"
        //at this point in time.
        final FieldConstraint[] newList = new FieldConstraint[this.constraints.length - 1];
        int newIdx = 0;
        for ( int i = 0; i < this.constraints.length; i++ ) {

            if ( i != idx ) {
                newList[newIdx] = this.constraints[i];
                newIdx++;
            }

        }
        this.constraints = newList;

    }


}
