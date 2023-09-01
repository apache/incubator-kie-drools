package org.drools.mvel.integrationtests.phreak;

public class Pair {
    private Object o1;
    private Object o2;
    
    public Pair(Object o1,
                Object o2) {
        super();
        this.o1 = o1;
        this.o2 = o2;
    }
    
    public static Pair t(Object o1, Object o2) {
        return new Pair(o1, o2);
    }
            
    
    public Object getO1() {
        return o1;
    }
    
    public Object getO2() {
        return o2;
    }
    
}
