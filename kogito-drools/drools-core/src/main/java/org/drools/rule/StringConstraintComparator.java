package org.drools.rule;

import org.drools.spi.ConstraintComparator;

public class StringConstraintComparator
    implements
    ConstraintComparator {
    public final int type;

    public StringConstraintComparator(int type){
        this.type = type;
    }

    public boolean compare(Object object1,
                           Object object2){
        boolean value = false;
        switch ( this.type ) {
            case ConstraintComparator.EQUAL :
                value = isEqual( object1,
                                 object2 );
                break;
            case ConstraintComparator.NOT_EQUAL :
                value = isNotEqual( object1,
                                    object2 );
                break;
        }
        return value;
    }

    private boolean isEqual(Object object1,
                            Object object2){
        return object1.equals( object2 );
    }

    private boolean isNotEqual(Object object1,
                               Object object2){
        return !object1.equals( object2 );
    }

}
