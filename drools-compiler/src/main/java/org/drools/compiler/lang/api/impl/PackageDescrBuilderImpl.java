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

package org.drools.compiler.lang.api.impl;

import org.drools.compiler.lang.api.AccumulateImportDescrBuilder;
import org.drools.compiler.lang.api.AttributeDescrBuilder;
import org.drools.compiler.lang.api.DeclareDescrBuilder;
import org.drools.compiler.lang.api.DescrBuilder;
import org.drools.compiler.lang.api.GlobalDescrBuilder;
import org.drools.compiler.lang.api.PackageDescrBuilder;
import org.drools.compiler.lang.descr.AccumulateImportDescr;
import org.drools.compiler.lang.descr.AttributeDescr;
import org.drools.compiler.lang.descr.BaseDescr;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.compiler.lang.api.FunctionDescrBuilder;
import org.drools.compiler.lang.api.ImportDescrBuilder;
import org.drools.compiler.lang.api.QueryDescrBuilder;
import org.drools.compiler.lang.api.RuleDescrBuilder;
import org.drools.compiler.lang.descr.FunctionImportDescr;
import org.kie.api.io.Resource;

/**
 * A builder implementation for PackageDescrs using a fluent API.
 */
public class PackageDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, PackageDescr>
    implements
    PackageDescrBuilder {

    private Resource resource;

    protected PackageDescrBuilderImpl() {
        super( null,
               new PackageDescr() );
    }

    private PackageDescrBuilderImpl(Resource resource) {
        this();
        this.resource = resource;
    }

    public static PackageDescrBuilder newPackage() {
        return new PackageDescrBuilderImpl();
    }

    public static PackageDescrBuilder newPackage(Resource resource) {
        return new PackageDescrBuilderImpl(resource);
    }

    /**
     * {@inheritDoc}
     */
    public PackageDescr getDescr() {
        return descr;
    }

    /**
     * {@inheritDoc}
     */
    public PackageDescrBuilder name( String name ) {
        descr.setNamespace( name );
        return this;
    }

    public ImportDescrBuilder newImport() {
        ImportDescrBuilder impl = new ImportDescrBuilderImpl( this, false );
        descr.addImport( initDescr(impl) );
        return impl;
    }

    public ImportDescrBuilder newFunctionImport() {
        ImportDescrBuilder impl = new ImportDescrBuilderImpl( this, true );
        descr.addFunctionImport( (FunctionImportDescr) initDescr(impl) );
        return impl;
    }

    public AccumulateImportDescrBuilder newAccumulateImport() {
        AccumulateImportDescrBuilder impl = new AccumulateImportDescrBuilderImpl( this );
        descr.addAccumulateImport( (AccumulateImportDescr) initDescr(impl) );
        return impl;
    }

    public GlobalDescrBuilder newGlobal() {
        GlobalDescrBuilder global = new GlobalDescrBuilderImpl( this );
        descr.addGlobal( initDescr(global) );
        return global;
    }

    public DeclareDescrBuilder newDeclare() {
        DeclareDescrBuilder declare = new DeclareDescrBuilderImpl( this );
        return declare;
    }

    public RuleDescrBuilder newRule() {
        RuleDescrBuilder rule = new RuleDescrBuilderImpl( this );
        descr.addRule( initDescr(rule) );
        return rule;
    }

    public QueryDescrBuilder newQuery() {
        QueryDescrBuilder query = new QueryDescrBuilderImpl( this );
        descr.addRule( initDescr(query) );
        return query;
    }

    public FunctionDescrBuilder newFunction() {
        FunctionDescrBuilder function = new FunctionDescrBuilderImpl( this );
        descr.addFunction( initDescr(function) );
        return function;
    }

    public AttributeDescrBuilder<PackageDescrBuilder> attribute( String name ) {
        AttributeDescrBuilder<PackageDescrBuilder> attribute = new AttributeDescrBuilderImpl<PackageDescrBuilder>( this, name );
        descr.addAttribute( initDescr(attribute) );
        return attribute;
    }

    private <T extends BaseDescr> T initDescr(DescrBuilder<PackageDescrBuilder, T> builder) {
        T descr = builder.getDescr();
        descr.setResource(resource);
        descr.setNamespace(descr.getNamespace());
        return descr;
    }

    public PackageDescrBuilder attribute( String name,
                                          String value ) {
        descr.addAttribute( new AttributeDescr( name,
                                                value ) );
        return this;
    }

    public PackageDescrBuilder attribute( String name,
                                          String value,
                                          AttributeDescr.Type type ) {
        descr.addAttribute( new AttributeDescr( name,
                                                value,
                                                type ) );
        return this;
    }

    public PackageDescrBuilder end() {
        return this;
    }
}
