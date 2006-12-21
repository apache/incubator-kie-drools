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
 */

package org.drools.semantics.java.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;
import org.drools.rule.Package;
import org.drools.rule.Query;
import org.drools.rule.Rule;
import org.drools.spi.AvailableVariables;

/**
 * A context for the current build
 * 
 * @author etirelli
 */
public class BuildContext {

    // current package
    private Package            pkg;

    // current rule
    private Rule               rule;

    // current Rule descriptor
    private RuleDescr          ruleDescr;

    // the class name for the rule
    private String             ruleClass;

    // declarations made in the current context
    private Map                declarations;

    // helper map for inner declarations
    private Map                innerDeclarations;

    // available variables 
    private AvailableVariables variables;

    // errors found when building the current context
    private List               errors;

    // list of generated methods
    private List               methods;

    // map<String invokerClassName, String invokerCode> of generated invokers
    private Map                invokers;

    // map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
    private Map                invokerLookups;

    // map<String invokerClassName, BaseDescr descr> of descriptor lookups
    private Map                descrLookups;

    // a simple counter for generated names
    private int                counter;

    // a simple counter for columns
    private int                columnCounter;

    // an offset counter for columns
    private int                columnOffset;

    /**
     * Default constructor
     */
    public BuildContext(final Package pkg,
                        final RuleDescr ruleDescr) {
        this.pkg = pkg;

        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.invokerLookups = new HashMap();
        this.declarations = new HashMap();
        this.descrLookups = new HashMap();
        this.declarations = new HashMap();
        this.errors = new ArrayList();
        this.variables = new AvailableVariables( new Map[]{this.declarations, this.pkg.getGlobals()} );
        this.ruleDescr = ruleDescr;

        if ( ruleDescr instanceof QueryDescr ) {
            this.rule = new Query( ruleDescr.getName() );
        } else {
            this.rule = new Rule( ruleDescr.getName() );
        }

    }

    /**
     * Returns the map of declarations for the current context
     * @return
     */
    public Map getDeclarations() {
        return declarations;
    }

    /**
     * Returns the list of errors found while building the current context
     * @return
     */
    public List getErrors() {
        return errors;
    }

    /**
     * Returns the current package being built
     * @return
     */
    public Package getPkg() {
        return pkg;
    }

    /**
     * Returns the current Rule being built
     * @return
     */
    public Rule getRule() {
        return rule;
    }

    /**
     * Returns the current RuleDescriptor
     * @return
     */
    public RuleDescr getRuleDescr() {
        return ruleDescr;
    }

    /**
     * Returns the available variables instance
     * @return
     */
    public AvailableVariables getVariables() {
        return variables;
    }

    /**
     * Sets the available variables instance
     * @param variables
     */
    public void setVariables(AvailableVariables variables) {
        this.variables = variables;
    }

    /**
     * Returns the Map<String invokerClassName, BaseDescr descr> of descriptor lookups
     * @return
     */
    public Map getDescrLookups() {
        return descrLookups;
    }

    public void setDescrLookups(Map descrLookups) {
        this.descrLookups = descrLookups;
    }

    /**
     * Returns the Map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
     * @return
     */
    public Map getInvokerLookups() {
        return invokerLookups;
    }

    public void setInvokerLookups(Map invokerLookups) {
        this.invokerLookups = invokerLookups;
    }

    /**
     * Returns the Map<String invokerClassName, String invokerCode> of generated invokers
     * @return
     */
    public Map getInvokers() {
        return invokers;
    }

    public void setInvokers(Map invokers) {
        this.invokers = invokers;
    }

    /**
     * Returns the list of generated methods
     * @return
     */
    public List getMethods() {
        return methods;
    }

    public void setMethods(List methods) {
        this.methods = methods;
    }

    /**
     * Returns current counter value for generated method names
     * @return
     */
    public int getCurrentId() {
        return counter;
    }

    public int getNextId() {
        return counter++;
    }

    public int getCurrentColumnId() {
        return columnCounter;
    }

    public int getNextColumnId() {
        return columnCounter++;
    }

    public int getColumnOffset() {
        return columnOffset;
    }

    public void setColumnOffset( int offset ) {
        this.columnOffset = offset;
    }

    public Map getInnerDeclarations() {
        return innerDeclarations;
    }

    public void setInnerDeclarations(Map innerDeclarations) {
        this.innerDeclarations = innerDeclarations;
    }

    public String getRuleClass() {
        return ruleClass;
    }

    public void setRuleClass(String ruleClass) {
        this.ruleClass = ruleClass;
    }

}
