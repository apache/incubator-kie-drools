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

package org.drools.workbench.models.commons.shared.rule;

import java.util.LinkedHashMap;
import java.util.Map;

public class ExpressionMethod extends ExpressionPart {

    private Map<String, ExpressionFormLine> params = new LinkedHashMap<String, ExpressionFormLine>();

    public ExpressionMethod() {
    }

    public ExpressionMethod( String methodName,
                             String returnClassType,
                             String returnGenericType ) {
        super( methodName, returnClassType, returnGenericType );
    }

    public ExpressionMethod( String name,
                             String classType,
                             String genericType,
                             String parametricType ) {
        super( name, classType, genericType, parametricType );
    }

    public Map<String, ExpressionFormLine> getParams() {
        return params;
    }

    public void setParams( Map<String, ExpressionFormLine> params ) {
        this.params.putAll( params );
    }

    public void putParam( String name,
                          ExpressionFormLine expression ) {
        this.params.put( name, expression );
    }

    protected String paramsToString() {
        if ( params.isEmpty() ) {
            return "";
        }
        String sep = ", ";
        StringBuilder s = new StringBuilder();
        for ( ExpressionFormLine expr : params.values() ) {
            s.append( sep ).append( expr.getText() );
        }
        return s.substring( sep.length() );
    }

    @Override
    public void accept( ExpressionVisitor visitor ) {
        visitor.visit( this );
    }
}
