package org.jbpm.persistence.session.objects;

public class MyVariableExtendingSerializable extends MyVariableSerializable {

    private static final long serialVersionUID = 1L;

    public MyVariableExtendingSerializable(String string) {
        super( string );
    }
    
    @Override
    public String toString() {
        return "Extended " + super.toString();
    }

}
