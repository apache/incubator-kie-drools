package org.drools.workbench.models.commons.backend.imports;

import org.drools.workbench.models.datamodel.imports.HasImports;
import org.drools.workbench.models.datamodel.imports.Imports;

/**
 * Writes import details to a String
 */
public class ImportsWriter {

    public static void write( final StringBuilder sb,
                              final HasImports model ) {
        final Imports imports = model.getImports();
        if ( imports == null ) {
            return;
        }
        sb.append( imports.toString() );
        if ( imports.getImports().size() > 0 ) {
            sb.append( "\n" );
        }
    }

}
