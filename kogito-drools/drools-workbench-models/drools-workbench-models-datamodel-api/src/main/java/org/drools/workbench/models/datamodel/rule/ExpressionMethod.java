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

package org.drools.workbench.models.datamodel.rule;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExpressionMethod extends ExpressionPart {

    private Map<ExpressionMethodParameterDefinition, ExpressionFormLine> params = new LinkedHashMap<ExpressionMethodParameterDefinition, ExpressionFormLine>();

    public ExpressionMethod() {
    }

    public ExpressionMethod( String methodName,
                             String returnClassType,
                             String returnGenericType ) {
        super( methodName,
               returnClassType,
               returnGenericType );
    }

    public ExpressionMethod( String name,
                             String classType,
                             String genericType,
                             String parametricType ) {
        super( name,
               classType,
               genericType,
               parametricType );
    }

    public Map<ExpressionMethodParameterDefinition, ExpressionFormLine> getParams() {
        return params;
    }

    public void setParams( Map<ExpressionMethodParameterDefinition, ExpressionFormLine> params ) {
        this.params.putAll( params );
    }

    public void putParam( String name,
                          ExpressionFormLine expression ) {
        this.params.put( new ExpressionMethodParameterDefinition( this.params.size(),
                                                                  name ),
                         expression );
    }

    public List<ExpressionFormLine> getOrderedParams() {
        final List<ExpressionFormLine> orderedParams = new ArrayList<ExpressionFormLine>();
        orderedParams.addAll( params.values() );
        Collections.sort( orderedParams,
                          new Comparator<ExpressionFormLine>() {
                              @Override
                              public int compare( final ExpressionFormLine o1,
                                                  final ExpressionFormLine o2 ) {
                                  return o1.getIndex() - o2.getIndex();
                              }
                          } );
        return orderedParams;
    }

    public String getParameterDataType( final ExpressionFormLine efl ) {
        for ( Map.Entry<ExpressionMethodParameterDefinition, ExpressionFormLine> e : params.entrySet() ) {
            if ( e.getValue().equals( efl ) ) {
                return e.getKey().getDataType();
            }
        }
        return null;
    }

    @Override
    public void accept( ExpressionVisitor visitor ) {
        visitor.visit( this );
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) {
            return true;
        }
        if ( o == null || getClass() != o.getClass() ) {
            return false;
        }

        ExpressionMethod that = (ExpressionMethod) o;

        if ( params != null ? !params.equals( that.params ) : that.params != null ) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = ~~result;
        result = 31 * result + ( params != null ? params.hashCode() : 0 );
        result = ~~result;
        return result;
    }
}
