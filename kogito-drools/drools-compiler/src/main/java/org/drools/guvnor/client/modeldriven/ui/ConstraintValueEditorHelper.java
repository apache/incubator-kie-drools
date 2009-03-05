package org.drools.guvnor.client.modeldriven.ui;

public class ConstraintValueEditorHelper {

    /**
     * 'Person.age' : ['M=Male', 'F=Female']
     *
     * This will split the drop down item into a value and a key.
     * eg key=value
     *
     */
    public static String[] splitValue(String v) {
        String[] s = new String[2];
        int pos = v.indexOf( '=' );
        s[0] = v.substring( 0, pos );
        s[1] = v.substring( pos + 1, v.length() );
        return s;
    }

}
