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

import org.drools.compiler.Dialect.AnalysisResult;
import org.drools.rule.builder.dialect.java.parser.JavaBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;

/**
 * An analysis result implementation for the java dialect
 * 
 * @author etirelli
 */
public class JavaAnalysisResult implements AnalysisResult {
    private static final List<String>[] EMPTY_ARRAY_OF_LISTS = new List[0];
    
    private List<String>[] boundIdentifiers = EMPTY_ARRAY_OF_LISTS;
    private List<String> identifiers = Collections.emptyList();
    private Map<String,JavaLocalDeclarationDescr> localVariables = Collections.emptyMap();
    private List<String> notBoundedIdentifiers = Collections.emptyList();
    private List<JavaBlockDescr> blocks = Collections.emptyList();
    
    public List<String>[] getBoundIdentifiers() {
        return boundIdentifiers;
    }
    public void setBoundIdentifiers(List<String>[] boundIdentifiers) {
        this.boundIdentifiers = boundIdentifiers;
    }
    public List<String> getIdentifiers() {
        return identifiers;
    }
    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }
    public List<String> getLocalVariables() {
        return new ArrayList<String>( localVariables.keySet() );
    }
    public Map<String,JavaLocalDeclarationDescr> getLocalVariablesMap() {
        return this.localVariables;
    }
    public void setLocalVariables(Map<String,JavaLocalDeclarationDescr> localVariables) {
        this.localVariables = localVariables;
    }
    public void addLocalVariable( String identifier, JavaLocalDeclarationDescr descr ) {
        this.localVariables.put( identifier, descr );
    }
    public List<String> getNotBoundedIdentifiers() {
        return notBoundedIdentifiers;
    }
    public void setNotBoundedIdentifiers(List<String> notBoundedIdentifiers) {
        this.notBoundedIdentifiers = notBoundedIdentifiers;
    }
    public List<JavaBlockDescr> getBlockDescrs() {
        return blocks;
    }
    public void setBlockDescrs(List<JavaBlockDescr> blocks) {
        this.blocks = blocks;
    }
}
