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
package org.drools.compiler.lang;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.drl.ast.descr.BindingDescr;
import org.drools.drl.ast.descr.OperatorDescr;

public class DumperContext {
    protected Map<String, OperatorDescr> aliases;
    protected int counter;
    protected List<BindingDescr> bindings;
    private RuleBuildContext ruleContext;
    private Map<String, String> inferredCasts;
    private int openCcd;

    public DumperContext() {
        this.aliases = new HashMap<>();
        this.counter = 0;
        this.bindings = null;
        this.openCcd = 0;
    }

    public void clear() {
        this.aliases.clear();
        this.counter = 0;
        this.bindings = null;
        this.openCcd = 0;
    }

    public void addInferredCast(String var, String cast) {
        if (inferredCasts == null) {
            inferredCasts = new HashMap<>();
        }
        inferredCasts.put(var, cast);
    }

    public Map.Entry<String, String> getInferredCast(String expr) {
        if (inferredCasts != null) {
            for (Map.Entry<String, String> entry : inferredCasts.entrySet()) {
                if (expr.matches(entry.getKey() + "\\s*\\..+")) {
                    return entry;
                }
            }
        }
        return null;
    }

    /**
     * @return the aliases
     */
    public Map<String, OperatorDescr> getAliases() {
        return aliases;
    }

    /**
     * @param aliases the aliases to set
     */
    public void setAliases( Map<String, OperatorDescr> aliases ) {
        this.aliases = aliases;
    }

    /**
     * Creates a new alias for the operator, setting it in the descriptor
     * class, adding it to the internal Map and returning it as a String
     */
    public String createAlias( OperatorDescr operator ) {
        String alias = operator.getOperator() + counter++;
        operator.setAlias(alias);
        this.aliases.put( alias,
                operator );
        return alias;
    }

    /**
     * Adds a binding to the list of bindings on this context
     */
    public void addBinding( BindingDescr bind ) {
        if( this.bindings == null ) {
            this.bindings = new ArrayList<>();
        }
        this.bindings.add( bind );
    }

    @SuppressWarnings("unchecked")
    public List<BindingDescr> getBindings() {
        return this.bindings == null ? Collections.EMPTY_LIST : this.bindings;
    }

    public RuleBuildContext getRuleContext() {
        return ruleContext;
    }

    public DumperContext setRuleContext(RuleBuildContext ruleContext) {
        this.ruleContext = ruleContext;
        return this;
    }

    public void incOpenCcd() {
        openCcd++;
    }

    public void decOpenCcd() {
        openCcd--;
    }

    public boolean isCcdNested() {
        return openCcd > 0;
    }

    public boolean isInXpath() {
        return ruleContext != null && ruleContext.isInXpath();
    }

    public void setInXpath( boolean inXpath ) {
        ruleContext.setInXpath( inXpath );
    }
}