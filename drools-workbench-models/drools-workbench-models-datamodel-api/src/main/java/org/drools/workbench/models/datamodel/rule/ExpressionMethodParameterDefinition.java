/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.workbench.models.datamodel.rule;

import org.drools.workbench.models.datamodel.util.PortablePreconditions;

/**
 * Meta Data for a ExpressionMethod's parameter definitions.
 */
public class ExpressionMethodParameterDefinition {

    private int index;
    private String dataType;

    public ExpressionMethodParameterDefinition() {
        //Empty constructor for Errai marshalling. Cannot use @MapsTo since we don't want any Errai JAR dependencies here.
    }

    public ExpressionMethodParameterDefinition( final int index,
                                                final String dataType ) {
        this.index = index;
        this.dataType = PortablePreconditions.checkNotNull( "dataType",
                                                            dataType );
    }

    public int getIndex() {
        return index;
    }

    public String getDataType() {
        return dataType;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( !( o instanceof ExpressionMethodParameterDefinition ) ) {
            return false;
        }

        ExpressionMethodParameterDefinition that = (ExpressionMethodParameterDefinition) o;

        if ( index != that.index ) {
            return false;
        }
        return dataType.equals( that.dataType );

    }

    @Override
    public int hashCode() {
        int result = index;
        result = 31 * result + dataType.hashCode();
        result = ~~result;
        return result;
    }
}
