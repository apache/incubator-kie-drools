package org.drools.core.util;


public class NumberUtils {

    /**
     * Checks if an addition of 2 longs caused an overflow 
     * @param op1
     * @param op2
     * @param result
     * @return true if an overflow occurred, false otherwise
     */
    public static boolean isAddOverflow( final long op1, final long op2, final long result ) {
        // ((op1^result)&(op2^result))<0) is a shorthand for:
        // ( (op1<0 && op2<0 && result>=0) ||
        //   (op1>0 && op2>0 && result<=0) )
        return (( (op1^result) & (op2^result) ) < 0);
    }
}
