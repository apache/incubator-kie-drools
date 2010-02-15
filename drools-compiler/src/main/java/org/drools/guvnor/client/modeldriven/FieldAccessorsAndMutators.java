package org.drools.guvnor.client.modeldriven;

public enum FieldAccessorsAndMutators {
    MUTATOR, ACCESSOR, BOTH;

    public static boolean compare(FieldAccessorsAndMutators field1,
                                  FieldAccessorsAndMutators field2) {

        if ( field1 == field2 ) {
            return true;
        } else if ( field1 == BOTH || field2 == BOTH ) {
            return true;
        } else {
            return false;
        }
    }

}
