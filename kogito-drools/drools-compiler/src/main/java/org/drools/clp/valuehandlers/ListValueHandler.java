package org.drools.clp.valuehandlers;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;

import org.drools.base.SimpleValueType;
import org.drools.clp.ExecutionContext;
import org.drools.clp.ValueHandler;

public class ListValueHandler
    implements
    ValueHandler {
    private ValueHandler[] list;

    public ListValueHandler() {

    }

    public ValueHandler getValue(ExecutionContext context) {
        return null;
    }

    public int getValueType(ExecutionContext context) {
        return SimpleValueType.LIST;
    }

    public void add(ListValueHandler list,
                    ExecutionContext context) {
        add( list.getList(),
             context );
    }

    public void add(ValueHandler[] valueHandlers,
                    ExecutionContext context) {
        for ( int i = 0, length = valueHandlers.length; i < length; i++ ) {
            add( valueHandlers[i],
                 context );
        }
    }

    public void add(ValueHandler valueHandler,
                    ExecutionContext context) {
        ValueHandler nested = valueHandler.getValue( context );
        valueHandler = (nested != null ) ? nested : valueHandler;
        if ( valueHandler instanceof ListValueHandler ) {
            add( (ListValueHandler) valueHandler,
                 context );
        } else {
            add( valueHandler );
        }
    }

    public void add(ValueHandler valueHandler) {
        if ( this.list == null ) {
            this.list = new ValueHandler[]{valueHandler};
        } else {
            ValueHandler[] temp = new ValueHandler[this.list.length + 1];
            System.arraycopy( this.list,
                              0,
                              temp,
                              0,
                              this.list.length );
            temp[temp.length - 1] = valueHandler;
            this.list = temp;
        }
    }

    public ValueHandler[] getList() {
        return this.list;
    }

    public BigDecimal getBigDecimalValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return BigDecimal" );
    }

    public BigInteger getBigIntegerValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return BigInteger" );
    }

    public boolean getBooleanValue(ExecutionContext context) throws ClassCastException {
        throw new RuntimeException( "ListValueHandler cannot return Boolean" );
    }

    public double getDoubleValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return Double" );
    }

    public float getFloatValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return Float" );
    }

    public int getIntValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return Int" );
    }

    public long getLongValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return Long" );
    }

    public short getShortValue(ExecutionContext context) throws NumberFormatException {
        throw new RuntimeException( "ListValueHandler cannot return Short" );
    }

    public String getStringValue(ExecutionContext context) {
        throw new RuntimeException( "ListValueHandler cannot return String" );
    }

    public Object getObject(ExecutionContext context) {
        Object[] objects = new Object[this.list.length];
        for ( int i = 0, length = this.list.length; i < length; i++ ) {
            objects[i] = this.list[i].getObject( context );
        }

        return objects;
    }

    public void setValue(ExecutionContext context,
                         Object object) {
        throw new RuntimeException( "cannot set value on ListValueHandler" );
    }

    public void replaceTempTokens(Map variables) {
        for ( int i = 0, length = this.list.length; i < length; i++ ) {
            if ( this.list[i] instanceof TempTokenVariable ) {
                TempTokenVariable var = (TempTokenVariable) this.list[i];
                this.list[i] = (ValueHandler) variables.get( var.getIdentifier() );
            } else if ( this.list[i] instanceof FunctionCaller ) {
                ((FunctionCaller) list[i]).replaceTempTokens( variables );
            } else if ( this.list[i] instanceof ListValueHandler ) {
                ((ListValueHandler) list[i]).replaceTempTokens( variables );
            }
        }
    }

    public int size() {
        return (this.list == null) ? 0 : this.list.length;
    }

    public boolean isEmpty() {
        return (this.list == null) ? true : this.list.length > 0;
    }

    public String toString() {
        StringBuffer b = new StringBuffer();
        b.append( "[ListValueHandler " );
        for ( int i = 0, length = this.list.length; i < length; i++ ) {
            b.append( this.list[i] + " " );
        }
        b.append( "]" );

        return b.toString();
    }
}
