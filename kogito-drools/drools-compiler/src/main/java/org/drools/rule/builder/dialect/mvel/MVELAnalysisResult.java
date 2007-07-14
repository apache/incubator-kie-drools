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
package org.drools.rule.builder.dialect.mvel;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.drools.compiler.Dialect.AnalysisResult;

/**
 * An analysis result implementation for the MVEL dialect
 * 
 * @author etirelli
 */
public class MVELAnalysisResult implements AnalysisResult {
    private static final List[] EMPTY_ARRAY_OF_LISTS = new List[0];
    
    private List[] boundIdentifiers = EMPTY_ARRAY_OF_LISTS;
    private List identifiers = Collections.EMPTY_LIST;
    private List localVariables = Collections.EMPTY_LIST;
    private List notBoundedIdentifiers = Collections.EMPTY_LIST;
    
    private Map mvelVariables;
    private Map mvelInputs;
    
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
        return this.localVariables;
    }
    public void setLocalVariables(List localVariables) {
        this.localVariables = localVariables;
    }
    public List getNotBoundedIdentifiers() {
        return notBoundedIdentifiers;
    }
    public void setNotBoundedIdentifiers(List notBoundedIdentifiers) {
        this.notBoundedIdentifiers = notBoundedIdentifiers;
    }

    public Map getMvelVariables() {
        return mvelVariables;
    }
    
    public void setMvelVariables(Map mvelVariables) {
        this.mvelVariables = mvelVariables;
    }
    
    
}
