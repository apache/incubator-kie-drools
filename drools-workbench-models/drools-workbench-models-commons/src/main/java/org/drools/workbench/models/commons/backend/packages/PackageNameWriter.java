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

package org.drools.workbench.models.commons.backend.packages;

import org.drools.workbench.models.datamodel.packages.HasPackageName;

/**
 * Writes Package details to a String
 */
public class PackageNameWriter {

    public static void write( final StringBuilder sb,
                              final HasPackageName model ) {
        final String packageName = model.getPackageName();
        if ( !( packageName == null || packageName.isEmpty() ) ) {
            sb.append( "package " ).append( packageName ).append( ";\n\n" );
        }
    }

}
