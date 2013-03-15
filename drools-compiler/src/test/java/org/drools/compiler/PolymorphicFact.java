package org.drools.compiler;

public class PolymorphicFact {
    private Object data;

    public PolymorphicFact() {
        this( null );
    }
    
    public PolymorphicFact(Object data) {
        super();
        this.data = data;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }


}
