package org.drools.workbench.models.testscenarios.shared;

import java.util.HashMap;
import java.util.Map;

public class CallMethod
        implements
        Fixture {

    /*
     * the function name was not yet choose
     */
    public static final int TYPE_UNDEFINED = 0;

    /**
     * The function has been choosen
     */
    public static final int TYPE_DEFINED = 1;
    /*
     * shows the state of the method call TYPE_UNDEFINED => the user has not
     * choosen a method or TYPE_DEFINED => The user has choosen a function
     */
    private int state;

    private String methodName;

    private String variable;
    private CallFieldValue[] callFieldValues = new CallFieldValue[ 0 ];

    public CallMethod() {
    }

    public CallMethod( String variable ) {
        this.variable = variable;
    }

    public CallMethod( String variable,
                       String methodName ) {
        this.methodName = methodName;
        this.variable = variable;
    }

    public void removeField( final int idx ) {

        final CallFieldValue[] newList = new CallFieldValue[ this.callFieldValues.length - 1 ];
        int newIdx = 0;
        for ( int i = 0; i < this.callFieldValues.length; i++ ) {

            if ( i != idx ) {
                newList[ newIdx ] = this.callFieldValues[ i ];
                newIdx++;
            }

        }
        this.callFieldValues = newList;
    }

    public void addFieldValue( final CallFieldValue val ) {
        if ( this.callFieldValues == null ) {
            this.callFieldValues = new CallFieldValue[ 1 ];
            this.callFieldValues[ 0 ] = val;
        } else {
            final CallFieldValue[] newList = new CallFieldValue[ this.callFieldValues.length + 1 ];
            for ( int i = 0; i < this.callFieldValues.length; i++ ) {
                newList[ i ] = this.callFieldValues[ i ];
            }
            newList[ this.callFieldValues.length ] = val;
            this.callFieldValues = newList;
        }
    }

    public void setState( final int state ) {
        this.state = state;
    }

    public int getState() {
        return state;
    }

    public void setMethodName( final String methodName ) {
        this.methodName = methodName;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setVariable( final String variable ) {
        this.variable = variable;
    }

    public String getVariable() {
        return variable;
    }

    public void setCallFieldValues( final CallFieldValue[] callFieldValues ) {
        this.callFieldValues = callFieldValues;
    }

    public CallFieldValue[] getCallFieldValues() {
        return callFieldValues;

    }

    public Map<String, String> getCallFieldValuesMap() {
        Map<String, String> result = new HashMap<String, String>();

        for ( CallFieldValue callFieldValue : callFieldValues ) {
            result.put( callFieldValue.getField(), callFieldValue.getValue() );
        }

        return result;
    }

}
