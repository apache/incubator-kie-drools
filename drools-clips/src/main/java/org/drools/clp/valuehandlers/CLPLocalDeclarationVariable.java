package org.drools.clp.valuehandlers;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;
import org.drools.clp.VariableValueHandler;
import org.drools.rule.Declaration;

public class CLPLocalDeclarationVariable implements VariableValueHandler {
    
    private Declaration declaration;
       
    public CLPLocalDeclarationVariable(Declaration declaration) {
        this.declaration = declaration;
    }  
    
    public ValueHandler getValue(ExecutionContext context) {
        return this;
    }    
    
    public String getIdentifier() {
        return this.declaration.getIdentifier();
    }

    public Class getKnownType() {
        return declaration.getExtractor().getExtractToClass();
    }
    
    public int getValueType(ExecutionContext context) {
        return this.declaration.getValueType().getSimpleType(); 
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getIdentifier() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }
    
    public Object getObject(ExecutionContext context) {
        return declaration.getValue( context.getObject() );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        Object object = declaration.getValue( context.getObject() );
        if ( object instanceof BigDecimal ) {
            return (BigDecimal) object;
        } else {
            return new BigDecimal( object.toString() );
        }
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        Object object = declaration.getValue( context.getObject() );
        if ( object instanceof BigInteger ) {
            return (BigInteger) object;
        } else {
            return new BigInteger( object.toString() );
        }
    }    

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return declaration.getBooleanValue( context.getObject() );
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return declaration.getDoubleValue( context.getObject() );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return declaration.getFloatValue( context.getObject() );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return declaration.getIntValue( context.getObject() );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return declaration.getLongValue( context.getObject() );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return declaration.getShortValue( context.getObject() );
    }

    public String getStringValue(ExecutionContext context) {
        return (String) declaration.getValue( context.getObject() );
    }
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }  
    
    public boolean equals(ValueHandler other, ExecutionContext context) {
        // FIXME
        return false;
    }    
}
