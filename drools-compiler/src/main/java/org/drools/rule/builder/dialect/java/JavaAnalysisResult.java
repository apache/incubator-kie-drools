/*
 * Copyright 2006 JBoss Inc
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
 *
 * Created on Jun 18, 2007
 */
package org.drools.rule.builder.dialect.java;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.rule.builder.Dialect.AnalysisResult;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;

/**
 * An analysis result implementation for the java dialect
 * 
 * @author etirelli
 */
public class JavaAnalysisResult implements AnalysisResult {
    private static final List[] EMPTY_ARRAY_OF_LISTS = new List[0];
    
    private List[] boundIdentifiers = EMPTY_ARRAY_OF_LISTS;
    private List identifiers = Collections.EMPTY_LIST;
    private Map localVariables = Collections.EMPTY_MAP;
    private List notBoundedIdentifiers = Collections.EMPTY_LIST;
    
    public List[] getBoundIdentifiers() {
        return boundIdentifiers;
    }
    public void setBoundIdentifiers(List[] boundIdentifiers) {
        this.boundIdentifiers = boundIdentifiers;
    }
    public List getIdentifiers() {
        return identifiers;
    }
    public void setIdentifiers(List identifiers) {
        this.identifiers = identifiers;
    }
    public List getLocalVariables() {
        return new ArrayList( localVariables.keySet() );
    }
    public Map getLocalVariablesMap() {
        return this.localVariables;
    }
    public void setLocalVariables(Map localVariables) {
        this.localVariables = localVariables;
    }
    public void addLocalVariable( String identifier, JavaLocalDeclarationDescr descr ) {
        this.localVariables.put( identifier, descr );
    }
    public List getNotBoundedIdentifiers() {
        return notBoundedIdentifiers;
    }
    public void setNotBoundedIdentifiers(List notBoundedIdentifiers) {
        this.notBoundedIdentifiers = notBoundedIdentifiers;
    }
}
