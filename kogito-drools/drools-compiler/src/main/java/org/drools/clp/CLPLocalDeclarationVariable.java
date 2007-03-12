package org.drools.clp;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.drools.base.ValueType;
import org.drools.common.InternalFactHandle;
import org.drools.rule.Declaration;
import org.mvel.integration.VariableResolver;

public class CLPLocalDeclarationVariable implements VariableValueHandler {
    
    private Declaration declaration;
       
    public CLPLocalDeclarationVariable(Declaration declaration) {
        this.declaration = declaration;
    }        
    public String getIdentifier() {
        return this.declaration.getIdentifier();
    }

    public Class getKnownType() {
        return declaration.getExtractor().getExtractToClass();
    }
    
    public ValueType getValueType() {
        return this.declaration.getValueType();
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new UnsupportedOperationException( "External Variable identifer='" + getIdentifier() + "' type='" + getKnownType() + "' is final, it cannot be set" );
    }
    
    public Object getValue(ExecutionContext context) {
        return declaration.getValue( context.getObject() );
    }
    
    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        return (BigDecimal) declaration.getValue( context.getObject() );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        return (BigInteger) declaration.getValue( context.getObject() );
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
    
}
