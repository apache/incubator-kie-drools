package org.drools.util.asm;

public class MethodCompareA {

    public boolean evaluate(final String foo) {
        if ( foo == null || foo.startsWith( "42" ) ) {
            return true;
        } else {
            return false;
        }
    }

    public boolean askew(final Integer a) {
        return false;
    }

    public boolean evaluate2(final String foo) {
        if ( foo == null || foo.startsWith( "43" ) ) {
            return true;
        } else {
            return false;
        }
    }

}
