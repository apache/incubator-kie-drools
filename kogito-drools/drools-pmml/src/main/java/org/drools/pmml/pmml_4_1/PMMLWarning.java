package org.drools.pmml.pmml_4_1;

import org.drools.compiler.compiler.DroolsWarning;
import org.kie.api.io.Resource;

public class PMMLWarning extends DroolsWarning {

    private String message;

    public PMMLWarning( Resource resource, String message ) {
        super( resource );
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
