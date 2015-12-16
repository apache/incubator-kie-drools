/*
 * Copyright 2005 Red Hat, Inc. and/or its affiliates.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.lang.descr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QueryDescr extends RuleDescr {
    private static final long serialVersionUID = 520l;

    private List<String>      parameterTypes   = Collections.emptyList();
    private List<String>      parameterNames   = Collections.emptyList();
    
    public QueryDescr() {
        this( null,
              "" );
    }

    public QueryDescr(final String name) {
        this( name,
              "" );
    }

    public QueryDescr(final String ruleName,
                      final String documentation) {
        super( ruleName,
               documentation );
    }
    
    public void addParameter( String type, String variable ) {
        if( parameterTypes == Collections.EMPTY_LIST ) {
            this.parameterTypes = new ArrayList<String>();
            this.parameterNames = new ArrayList<String>();
        }
        this.parameterTypes.add( type );
        this.parameterNames.add( variable );
    }
    
    public String[] getParameters() {
        return this.parameterNames.toArray( new String[this.parameterNames.size()] );
    }
    
    public String[] getParameterTypes() {
        return this.parameterTypes.toArray( new String[this.parameterTypes.size()] );
    }

    public boolean isRule() {
        return false;
    }
    
    public boolean isQuery() {
        return true;
    }

    public String toString() {
        return "[Query name='" + getName() + "']";
    }
}
