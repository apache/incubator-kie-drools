package org.drools.pmml.pmml_4_2;

import org.drools.compiler.compiler.DroolsError;

public class PMMLError extends DroolsError {

    private String message;

    public PMMLError( String message ) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int[] getLines() {
        return new int[ 0 ];  //To change body of implemented methods use File | Settings | File Templates.
    }
}
