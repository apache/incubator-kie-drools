package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;

public class CLPPreviousDeclarationVariable implements VariableValueHandler {

    private Declaration declaration;
    
    public CLPPreviousDeclarationVariable(Declaration declaration) {
        this.declaration = declaration;
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
    
    public Object getValue(ExecutionContext context) {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getValue( handle.getObject() );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        Object object = declaration.getValue(  handle.getObject() );
        if ( object instanceof BigDecimal ) {
            return (BigDecimal) object;
        } else {
            return new BigDecimal( object.toString() );
        }
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        Object object = declaration.getValue(  handle.getObject() );
        if ( object instanceof BigInteger ) {
            return (BigInteger) object;
        } else {
            return new BigInteger( object.toString() );
        }
    }    

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getBooleanValue( handle.getObject() );
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getDoubleValue( handle.getObject() );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getFloatValue( handle.getObject() );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getIntValue( handle.getObject() );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getLongValue( handle.getObject() );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return declaration.getShortValue( handle.getObject() );
    }

    public String getStringValue(ExecutionContext context) {
        InternalFactHandle handle = context.getTuple().get( this.declaration );
        return (String) declaration.getValue( handle.getObject() );
    }
    
    public String toString() {
        String name = getClass().getName();
        name = name.substring( name.lastIndexOf( "." ) + 1 );
        return "[" + name + " identifier = '" + getIdentifier()  + "']";
    }    
        
}
