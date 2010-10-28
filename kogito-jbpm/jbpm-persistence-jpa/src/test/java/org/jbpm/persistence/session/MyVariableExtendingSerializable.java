package org.jbpm.persistence.session;

public class MyVariableExtendingSerializable extends MyVariableSerializable {

    public MyVariableExtendingSerializable(String string) {
        super( string );
    }
    
    @Override
    public String toString() {
        return "Extended " + super.toString();
    }

}
