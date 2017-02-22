/*
 * Copyright 2006 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.rule.builder;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.Dialect;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.DroolsWarning;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.rule.builder.dialect.mvel.MVELDialect;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.rule.Dialectable;
import org.drools.core.rule.MVELDialectRuntimeData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A context for the current build
 */
public class PackageBuildContext {

    // current package
    private InternalKnowledgePackage    pkg;

    private KnowledgeBuilderImpl        kBuilder;

    // the contianer descr
    private BaseDescr                   parentDescr;

    // errors found when building the current context
    private List<DroolsError>           errors;
    private List<DroolsWarning>         warnings;

    // list of generated methods
    private List<String>                methods;

    // map<String invokerClassName, String invokerCode> of generated invokers
    private Map<String, String>         invokers;

    // map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
    private Map<String, Object>         invokerLookups;

    // map<String invokerClassName, BaseDescr descr> of descriptor lookups
    private Map<String, BaseDescr>      descrLookups;

    // a simple counter for generated names
    private int                         counter;

    private DialectCompiletimeRegistry  dialectRegistry;

    private Dialect                     dialect;
    
    private boolean                     typesafe;

    public PackageBuildContext() {

    }

    /**
     * Default constructor
     */
    public void init(final KnowledgeBuilderImpl kBuilder,
                     final InternalKnowledgePackage pkg,
                     final BaseDescr parentDescr,
                     final DialectCompiletimeRegistry dialectRegistry,
                     final Dialect defaultDialect,
                     final Dialectable component) {
        this.kBuilder = kBuilder;
        
        this.pkg = pkg;

        this.parentDescr = parentDescr;

        this.methods = new ArrayList();
        this.invokers = new HashMap<String, String>();
        this.invokerLookups = new HashMap<String, Object>();
        this.descrLookups = new HashMap<String, BaseDescr>();
        this.errors = new ArrayList<DroolsError>();
        this.warnings = new ArrayList<DroolsWarning>();

        this.dialectRegistry = dialectRegistry;

        this.dialect = (component != null && component.getDialect() != null) ? this.dialectRegistry.getDialect( component.getDialect() ) : defaultDialect;

        this.typesafe = ((MVELDialect) dialectRegistry.getDialect( "mvel" )).isStrictMode();

        if ( dialect == null && (component != null && component.getDialect() != null) ) {
            this.errors.add( new DescrBuildError( null,
                                                  parentDescr,
                                                  component,
                                                  "Unable to load Dialect '" + component.getDialect() + "'" ) );
            // dialect is null, but fall back to default dialect so we can attempt to compile rest of rule.
            this.dialect = defaultDialect;
        }
    }

    public BaseDescr getParentDescr() {
        return this.parentDescr;
    }

    public void setParentDescr(BaseDescr descr) {
        this.parentDescr = descr;
    }

    public Dialect getDialect() {
        return dialect;
    }

    /**
     * Allows the change of the current dialect in the context
     */
    public void setDialect(Dialect dialect) {
        this.dialect = dialect;
    }

    public Dialect getDialect(String dialectName) {
        return (Dialect) this.dialectRegistry.getDialect( dialectName );
    }

    public DialectCompiletimeRegistry getDialectRegistry() {
        return this.dialectRegistry;
    }

    /**
     * Returns the list of errors found while building the current context
     * @return
     */
    public List<DroolsError> getErrors() {
        return this.errors;
    }

    public void addError(DroolsError error) {
        errors.add(error);
    }

    public List<DroolsWarning> getWarnings() {
        return warnings;
    }

    public void addWarning( DroolsWarning warning ) {
        this.warnings.add( warning );
    }

    /**
     * Returns the current package being built
     * @return
     */
    public InternalKnowledgePackage getPkg() {
        return this.pkg;
    }

    /**
     * Returns the Map<String invokerClassName, BaseDescr descr> of descriptor lookups
     * @return
     */
    public BaseDescr getDescrLookup(String className) {
        return descrLookups.get(className);
    }

    public void addDescrLookups(String className, BaseDescr baseDescr) {
        descrLookups.put(className, baseDescr);
    }

    public Object getInvokerLookup(String className) {
        return invokerLookups.get(className);
    }

    public void addInvokerLookup(String className, Object invokerLookup) {
        invokerLookups.put(className, invokerLookup);
    }

    /**
     * Returns the Map<String invokerClassName, String invokerCode> of generated invokers
     * @return
     */
    public Map<String, String> getInvokers() {
        return this.invokers;
    }

    public void setInvokers(final Map<String, String> invokers) {
        this.invokers = invokers;
    }

    /**
     * Returns the list of generated methods
     * @return
     */
    public List<String> getMethods() {
        return this.methods;
    }

    public void addMethod(String method) {
        this.methods.add(method);
    }

    /**
     * Returns current counter value for generated method names
     * @return
     */
    public int getCurrentId() {
        return this.counter;
    }

    public int getNextId() {
        return this.counter++;
    }

    public KnowledgeBuilderConfigurationImpl getConfiguration() {
        return this.kBuilder.getBuilderConfiguration();
    }
    
    public KnowledgeBuilderImpl getKnowledgeBuilder() {
        return this.kBuilder;
    }

    public boolean isTypesafe() {
        return typesafe;
    }

    public void setTypesafe(boolean stricttype) {
        this.typesafe = stricttype;
    }

    public MVELDialectRuntimeData getMVELDialectRuntimeData() {
        return ( MVELDialectRuntimeData) pkg.getDialectRuntimeRegistry().getDialectData( "mvel" );
    }
}
