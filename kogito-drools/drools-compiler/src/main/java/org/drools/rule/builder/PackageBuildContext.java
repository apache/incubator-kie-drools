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

package org.drools.rule.builder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.DescrBuildError;
import org.drools.compiler.Dialect;
import org.drools.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.PackageBuilder;
import org.drools.compiler.PackageBuilderConfiguration;
import org.drools.lang.descr.BaseDescr;
import org.drools.rule.Dialectable;
import org.drools.rule.Package;

/**
 * A context for the current build
 * 
 * @author etirelli
 */
public class PackageBuildContext {

    // current package
    private Package                     pkg;

    private PackageBuilder pkgBuilder;

    // the contianer descr
    private BaseDescr                   parentDescr;

    // errors found when building the current context
    private List                        errors;

    // list of generated methods
    private List                        methods;

    // map<String invokerClassName, String invokerCode> of generated invokers
    private Map                         invokers;

    // map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
    private Map                         invokerLookups;

    // map<String invokerClassName, BaseDescr descr> of descriptor lookups
    private Map                         descrLookups;

    // a simple counter for generated names
    private int                         counter;

    private DialectCompiletimeRegistry  dialectRegistry;

    private Dialect                     dialect;

    public PackageBuildContext() {

    }

    /**
     * Default constructor
     */
    public void init(final PackageBuilder pkgBuilder,
                     final Package pkg,
                     final BaseDescr parentDescr,
                     final DialectCompiletimeRegistry dialectRegistry,
                     final Dialect defaultDialect,
                     final Dialectable component) {
        this.pkgBuilder = pkgBuilder;
        
        this.pkg = pkg;

        this.parentDescr = parentDescr;

        this.methods = new ArrayList();
        this.invokers = new HashMap();
        this.invokerLookups = new HashMap();
        this.descrLookups = new HashMap();
        this.errors = new ArrayList();

        this.dialectRegistry = dialectRegistry;

        this.dialect = (component != null && component.getDialect() != null) ? this.dialectRegistry.getDialect( component.getDialect() ) : defaultDialect;

        if ( dialect == null && (component != null && component.getDialect() != null) ) {
            this.errors.add( new DescrBuildError( null,
                                                  parentDescr,
                                                  component,
                                                  "Unable to load Dialect '" + component.getDialect() + "'" ) );
        }
    }

    public BaseDescr getParentDescr() {
        return this.parentDescr;
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
    public List getErrors() {
        return this.errors;
    }

    /**
     * Returns the current package being built
     * @return
     */
    public Package getPkg() {
        return this.pkg;
    }

    /**
     * Returns the Map<String invokerClassName, BaseDescr descr> of descriptor lookups
     * @return
     */
    public Map getDescrLookups() {
        return this.descrLookups;
    }

    public void setDescrLookups(final Map descrLookups) {
        this.descrLookups = descrLookups;
    }

    /**
     * Returns the Map<String invokerClassName, ConditionalElement ce> of generated invoker lookups
     * @return
     */
    public Map getInvokerLookups() {
        return this.invokerLookups;
    }

    public void setInvokerLookups(final Map invokerLookups) {
        this.invokerLookups = invokerLookups;
    }

    /**
     * Returns the Map<String invokerClassName, String invokerCode> of generated invokers
     * @return
     */
    public Map getInvokers() {
        return this.invokers;
    }

    public void setInvokers(final Map invokers) {
        this.invokers = invokers;
    }

    /**
     * Returns the list of generated methods
     * @return
     */
    public List getMethods() {
        return this.methods;
    }

    public void setMethods(final List methods) {
        this.methods = methods;
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

    public PackageBuilderConfiguration getConfiguration() {
        return this.pkgBuilder.getPackageBuilderConfiguration();
    }
    
    public PackageBuilder getPackageBuilder() {
        return this.pkgBuilder;
    }

}
