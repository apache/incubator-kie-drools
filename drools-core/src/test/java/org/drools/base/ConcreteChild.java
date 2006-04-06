package org.drools.base;

import org.drools.util.asm.InterfaceChild;

public class ConcreteChild
    implements
    InterfaceChild {

    public String getBar() {
        
        return "hola";
    }

    public int getFoo() {
        
        return 42;
    }

    public int getBaz() {

        return 42;
    }

}
