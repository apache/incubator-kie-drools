/*
 * Copyright 2012 JBoss Inc
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

package org.drools.workbench.models.datamodel.oracle;

import java.util.Iterator;
import java.util.List;

public class MethodInfo {

    private String name;
    private List<String> params;
    private String returnClassType;
    private String parametricReturnType;
    private String genericType;

    public MethodInfo() {
    }

    /**
     * @param name method name
     * @param params method params list
     * @param returnType method's return type
     */
    public MethodInfo( final String name,
                       final List<String> params,
                       final Class<?> returnType,
                       final String parametricReturnType,
                       final String genericType ) {
        this.name = name;
        this.params = params;
        this.returnClassType = returnType.getName();
        this.parametricReturnType = parametricReturnType;
        this.genericType = genericType;
    }

    /**
     * @param name method name
     * @param params method params list
     * @param returnType method's return type
     */
    public MethodInfo( final String name,
                       final List<String> params,
                       final String returnType,
                       final String parametricReturnType,
                       final String genericType ) {
        this.name = name;
        this.params = params;
        this.returnClassType = returnType;
        this.parametricReturnType = parametricReturnType;
        this.genericType = genericType;
    }

    public String getNameWithParameters() {
        if ( params.isEmpty() ) {
            return name + "()";
        }
        StringBuilder p = new StringBuilder();

        for ( Iterator<String> iterator = params.iterator(); iterator.hasNext(); ) {
            p.append( ", " ).append( iterator.next() );
        }

        return name + "(" + p.substring( 2 ) + ")";
    }

    public String getName() {
        return name;
    }

    public List<String> getParams() {
        return params;
    }

    public String getReturnClassType() {
        return returnClassType;
    }

    public String getParametricReturnType() {
        return parametricReturnType;
    }

    public String getGenericType() {
        return genericType;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ( ( name == null ) ? 0 : name.hashCode() );
        result = ~~result;
        result = prime
                * result
                + ( ( parametricReturnType == null ) ? 0 : parametricReturnType
                .hashCode() );
        result = ~~result;
        result = prime * result + ( ( params == null ) ? 0 : params.hashCode() );
        result = ~~result;
        result = prime * result
                + ( ( returnClassType == null ) ? 0 : returnClassType.hashCode() );
        result = ~~result;
        return result;
    }

    @Override
    public boolean equals( Object obj ) {
        if ( this == obj ) {
            return true;
        }
        if ( obj == null ) {
            return false;
        }
        if ( getClass() != obj.getClass() ) {
            return false;
        }
        MethodInfo other = (MethodInfo) obj;
        if ( name == null ) {
            if ( other.name != null ) {
                return false;
            }
        } else if ( !name.equals( other.name ) ) {
            return false;
        }
        if ( parametricReturnType == null ) {
            if ( other.parametricReturnType != null ) {
                return false;
            }
        } else if ( !parametricReturnType.equals( other.parametricReturnType ) ) {
            return false;
        }
        if ( params == null ) {
            if ( other.params != null ) {
                return false;
            }
        } else if ( !params.equals( other.params ) ) {
            return false;
        }
        if ( returnClassType == null ) {
            if ( other.returnClassType != null ) {
                return false;
            }
        } else if ( !returnClassType.equals( other.returnClassType ) ) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return getNameWithParameters();
    }
}
