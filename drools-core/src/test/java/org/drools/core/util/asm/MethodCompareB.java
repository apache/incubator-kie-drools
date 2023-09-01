package org.drools.core.util.asm;

public class MethodCompareB {

    public boolean evaluate(final String foox) {
        if ( foox == null || foox.startsWith( "42" ) ) {

            return true;
        } else {
            return false;
        }
    }

}
