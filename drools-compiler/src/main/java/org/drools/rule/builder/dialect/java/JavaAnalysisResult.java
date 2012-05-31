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

import org.drools.compiler.AnalysisResult;
import org.drools.compiler.BoundIdentifiers;
import org.drools.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
import org.drools.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * An analysis result implementation for the java dialect
 */
public class JavaAnalysisResult implements AnalysisResult {

    private String analyzedExpr;
    private BoundIdentifiers boundIdentifiers = null;
    private Set<String> identifiers = Collections.emptySet();
    private Map<String,JavaLocalDeclarationDescr> localVariables = Collections.emptyMap();
    private Set<String> notBoundedIdentifiers = Collections.emptySet();
    private JavaContainerBlockDescr blocks;

    public String getAnalyzedExpr() {
        return analyzedExpr;
    }
    public void setAnalyzedExpr(String analyzedExpr) {
        this.analyzedExpr = analyzedExpr;
    }
    public BoundIdentifiers getBoundIdentifiers() {
        return boundIdentifiers;
    }
    public void setBoundIdentifiers(BoundIdentifiers boundIdentifiers) {
        this.boundIdentifiers = boundIdentifiers;
    }
    public Set<String> getIdentifiers() {
        return identifiers;
    }
    public void setIdentifiers(Set<String> identifiers) {
        this.identifiers = identifiers;
    }
    public Set<String> getLocalVariables() {
        return localVariables.keySet();
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
    public Set<String> getNotBoundedIdentifiers() {
        return notBoundedIdentifiers;
    }
    public void setNotBoundedIdentifiers(Set<String> notBoundedIdentifiers) {
        this.notBoundedIdentifiers = notBoundedIdentifiers;
    }
    public JavaContainerBlockDescr getBlockDescrs() {
        return blocks;
    }
    public void setBlockDescrs(JavaContainerBlockDescr blocks) {
        this.blocks = blocks;
    }
}
