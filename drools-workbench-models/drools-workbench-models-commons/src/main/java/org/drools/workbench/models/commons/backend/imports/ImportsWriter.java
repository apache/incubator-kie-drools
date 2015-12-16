/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

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
