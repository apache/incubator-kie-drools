package org.drools.core.reteoo.sequencing;

public class Gates {
    public static boolean and(long a, long b) {return a == b;}

    public static boolean or(long a, long b)  {return (a & b) > 0;}


    public static boolean nor(long a, long b) {return (a | b) == 0;}
    //public static boolean nor(long a, long b) { return  (~(a | b)) > 0;}

    public static boolean nand(long a, long b) {return (a & b) == 0;}
    //public static boolean nand(long a, long b) { return  (~(a & b)) > 0;}

    //((v1 & v2) > 0) && ((v1 & v2) != v1)
    //                               && (((v1 & v2) & ((v1 & v2)-1) )==0)
    public static boolean xor(long a, long b) {
        long v = a & b;
        return v > 0 && v != b && (v & -v) == 0;
    }
    //public static boolean xor(long a, long b) { return  (a ^ b) > 0;}

    //public static boolean xnor(long a, long b) { return  (~(a ^ b)) > 0;}
    public static boolean xnor(long a, long b) {
        long v = a & b;
        return v == 0 || v == b;
    }
}
