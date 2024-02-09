/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.java;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.compiler.AnalysisResult;
import org.drools.compiler.compiler.BoundIdentifiers;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaContainerBlockDescr;
import org.drools.compiler.rule.builder.dialect.java.parser.JavaLocalDeclarationDescr;

/**
 * An analysis result implementation for the java dialect
 */
public class JavaAnalysisResult implements AnalysisResult {

    private final String analyzedExpr;
    private final Set<String> identifiers;

    private BoundIdentifiers boundIdentifiers = null;
    private Map<String,JavaLocalDeclarationDescr> localVariables;
    private Set<String> notBoundedIdentifiers;
    private Set<String> assignedVariables;
    private JavaContainerBlockDescr blocks;

    public JavaAnalysisResult( String analyzedExpr, Set<String> identifiers ) {
        this.analyzedExpr = analyzedExpr;
        this.identifiers = identifiers;
    }

    public String getAnalyzedExpr() {
        return analyzedExpr;
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

    public Set<String> getLocalVariables() {
        return localVariables.keySet();
    }

    public Map<String,JavaLocalDeclarationDescr> getLocalVariablesMap() {
        return localVariables == null ? Collections.emptyMap() : localVariables;
    }
    public void addLocalVariable( String identifier, JavaLocalDeclarationDescr descr ) {
        if (localVariables == null) {
            localVariables = new HashMap<>();
        }
        this.localVariables.put( identifier, descr );
    }

    public Set<String> getNotBoundedIdentifiers() {
        return notBoundedIdentifiers == null ? Collections.emptySet() : notBoundedIdentifiers;
    }
    public void setNotBoundedIdentifiers(Set<String> notBoundedIdentifiers) {
        this.notBoundedIdentifiers = notBoundedIdentifiers;
    }

    public Set<String> getAssignedVariables() {
        return assignedVariables == null ? Collections.emptySet() : assignedVariables;
    }
    public void setAssignedVariables( Set<String> assignedVariables ) {
        this.assignedVariables = assignedVariables;
    }

    public JavaContainerBlockDescr getBlockDescrs() {
        return blocks;
    }
    public void setBlockDescrs(JavaContainerBlockDescr blocks) {
        this.blocks = blocks;
    }

    @Override
    public Class<?> getReturnType() {
        throw new UnsupportedOperationException();
    }
}
