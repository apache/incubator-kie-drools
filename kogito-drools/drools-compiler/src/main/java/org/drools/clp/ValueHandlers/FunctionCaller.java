package org.drools.clp.ValueHandlers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.Function;
import org.drools.clp.LispList;
import org.drools.clp.ValueHandler;

public class FunctionCaller
    implements
    ValueHandler {
    private Function         function;

    protected ValueHandler[] parameters;

    public FunctionCaller() {
        this( null );
    }

    public FunctionCaller(Function function) {
        this.function = function;
    }

    public int getValueType(ExecutionContext context) {
        return SimpleValueType.FUNCTION;
    }

    public void setFunction(Function function) {
        this.function = function;
    }

    public Function getFunction() {
        return this.function;
    }

    public String getName() {
        return (this.function == null) ? null : this.function.getName();
    }

    public LispList createList(int index) {
        return this.function.createList( index );
    }

    public void addParameter(ValueHandler valueHandler) {
        if ( this.parameters == null ) {
            this.parameters = new ValueHandler[]{valueHandler};
        } else {
            ValueHandler[] temp = new ValueHandler[parameters.length + 1];
            System.arraycopy( this.parameters,
                              0,
                              temp,
                              0,
                              this.parameters.length );
            temp[temp.length - 1] = valueHandler;
            this.parameters = temp;
        }
    }

    public ValueHandler[] getParameters() {
        return this.parameters;
    }

    private ValueHandler resolveFunction(FunctionCaller caller,
                                         ExecutionContext context) {
        ValueHandler result = caller.getFunction().execute( caller.getParameters(),
                                                            context );
        if ( result instanceof FunctionCaller ) {
            result = resolveFunction( (FunctionCaller) result,
                                      context );
        }
        return result;
    }
    
    public Object getValue(ExecutionContext context) {
        return resolveFunction( this,
                                context ).getValue( context );
    }    

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new RuntimeException( "You cannot set the value on a Function" );
    }

    public void replaceTempTokens(Map variables) {
        for ( int i = 0, length = this.parameters.length; i < length; i++ ) {
            if ( this.parameters[i] instanceof TempTokenVariable ) {
                TempTokenVariable var = (TempTokenVariable) this.parameters[i];
                this.parameters[i] = (ValueHandler) variables.get( var.getIdentifier() );
            } else if ( this.parameters[i] instanceof FunctionCaller ) {
                ((FunctionCaller) parameters[i]).replaceTempTokens( variables );
            } else if ( this.parameters[i] instanceof ListValueHandler ) {
                ((ListValueHandler) parameters[i]).replaceTempTokens( variables );
            }
        }
    }

    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getBigDecimalValue( context );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getBigIntegerValue( context );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        return resolveFunction( this, context ).getBooleanValue( context );
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getDoubleValue( context );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getFloatValue( context );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getIntValue( context );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getLongValue( context );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        return resolveFunction( this, context ).getShortValue( context );
    }

    public String getStringValue(ExecutionContext context) {
        return resolveFunction( this, context ).getStringValue( context );
    }

    public String toString() {
        return "[FunctionCaller " + this.function + "]";
    }

}
