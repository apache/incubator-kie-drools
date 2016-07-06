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

package org.drools.workbench.models.datamodel.oracle;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;

/**
 * Portable representation of an annotation
 */
public class Annotation {

    private String qualifiedTypeName;
    private Map<String, Object> parameters = new HashMap<String, Object>();

    public Annotation() {
        //Needed for Errai marshalling
    }

    public Annotation( final String qualifiedTypeName ) {
        PortablePreconditions.checkNotNull( "qualifiedTypeName",
                                            qualifiedTypeName );
        this.qualifiedTypeName = qualifiedTypeName;
    }

    public String getQualifiedTypeName() {
        return qualifiedTypeName;
    }

    public Map<String, Object> getParameters() {
        return Collections.unmodifiableMap( parameters );
    }

    public void addParameter( final String name,
                              final Object value ) {
        PortablePreconditions.checkNotNull( "name",
                                            name );
        this.parameters.put( name,
                             value );
    }

}
