/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.rule.builder.dialect.java.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class AbstractJavaContainerBlockDescr implements JavaContainerBlockDescr {
    private List<JavaBlockDescr> blocks = new ArrayList<JavaBlockDescr>();
    private Map<String, Class< ? >> inputs;
    private List<JavaLocalDeclarationDescr> inScopeLocalVars;
    
    public List<JavaBlockDescr> getJavaBlockDescrs() {
        return this.blocks;
    }      

    public void addJavaBlockDescr(JavaBlockDescr descr) {
        this.blocks.add( descr );
    }


    public Map<String, Class< ? >> getInputs() {
        return inputs;
    }

    public void setInputs(Map<String, Class< ? >> variables) {
        this.inputs = variables;
    }

    /**
     * Returns the list of in-code, declared variables that are available
     * in the scope of this block
     * @return
     */
    public List<JavaLocalDeclarationDescr> getInScopeLocalVars() {
        return inScopeLocalVars;
    }

    /**
     * Sets the list of in-code, declared variables that are available
     * in the scope of this block
     */
    public void setInScopeLocalVars( List<JavaLocalDeclarationDescr> inScopeLocalVars ) {
        this.inScopeLocalVars = inScopeLocalVars;
    }    
}
