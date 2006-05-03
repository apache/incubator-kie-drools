package org.drools.util.asm;

public class MethodCompareA {

    public boolean evaluate(String foo) {
        if (foo == null || foo.startsWith( "42" )) {
            return true;
        } else {
            return false;
        }
    }
    
    public boolean askew(Integer a) {
        return false;
    }
    
    public boolean evaluate2(String foo) {
        if (foo == null || foo.startsWith( "43" )) {
            return true;
        } else {
            return false;
        }
    }    
    
}
