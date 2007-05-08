package org.drools.clp.valuehandlers;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;

public class ObjectValueHandler extends BaseValueHandler {    
    private Object objectValue;       
    
    public ObjectValueHandler(Object objectValue) {
        this.objectValue = objectValue;
    }
    
    public int getValueType(ExecutionContext context) {
        return SimpleValueType.OBJECT;
    }
    
    public ValueHandler getValue(ExecutionContext context) {
        return this;
    }    
    
    public void setValue(ExecutionContext context, Object value) {
        throw new RuntimeException( "LiteralValues cannot be set");
    }
    
    public Object getObject(ExecutionContext context) {
        return this.objectValue;
    }    
    
}
