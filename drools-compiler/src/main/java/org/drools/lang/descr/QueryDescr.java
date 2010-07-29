/*
 * Copyright 2005 JBoss Inc
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

package org.drools.lang.descr;

public class QueryDescr extends RuleDescr {
    private String[] params;
    private String[] types;
    
    private static final String[] EMPTY_PARAMS = new String[0];
    /**
     * 
     */
    private static final long serialVersionUID = 510l;

    public QueryDescr(final String name) {
        this( name,
              "" );
        this.params = EMPTY_PARAMS;        
    }      

    public QueryDescr(final String ruleName,
                      final String documentation) {
        super( ruleName,
               documentation );
        this.params = EMPTY_PARAMS;         
    }
    
    public QueryDescr(final String ruleName,
                      final String documentation, 
                      final String[] params) {
        super( ruleName,
               documentation );
        this.params = params;         
    }
    
    public void setParameters(String[] params) {
        this.params = params;
    }
    
    public String[] getParameters() {
        return this.params;
    }
    
    public void setParameterTypes(String[] types) {
        this.types = types;
    }
    
    public String[] getParameterTypes() {
        return this.types;
    }    
}
