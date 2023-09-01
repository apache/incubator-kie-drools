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
package org.drools.drl.ast.dsl.impl;

import org.drools.drl.ast.dsl.AccumulateImportDescrBuilder;
import org.drools.drl.ast.dsl.AttributeDescrBuilder;
import org.drools.drl.ast.dsl.DeclareDescrBuilder;
import org.drools.drl.ast.dsl.DescrBuilder;
import org.drools.drl.ast.dsl.FunctionDescrBuilder;
import org.drools.drl.ast.dsl.GlobalDescrBuilder;
import org.drools.drl.ast.dsl.ImportDescrBuilder;
import org.drools.drl.ast.dsl.PackageDescrBuilder;
import org.drools.drl.ast.dsl.QueryDescrBuilder;
import org.drools.drl.ast.dsl.RuleDescrBuilder;
import org.drools.drl.ast.dsl.UnitDescrBuilder;
import org.drools.drl.ast.descr.AccumulateImportDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.BaseDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
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
        this.descr.setResource(resource);
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

    public UnitDescrBuilder newUnit() {
        UnitDescrBuilder impl = new UnitDescrBuilderImpl( this );
        descr.setUnit( initDescr(impl) );
        return impl;
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
        descr.addAccumulateImport(initDescr(impl));
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
        rule.getDescr().setUnit( descr.getUnit() );
        return rule;
    }

    public QueryDescrBuilder newQuery() {
        QueryDescrBuilder query = new QueryDescrBuilderImpl( this );
        descr.addRule( initDescr(query) );
        query.getDescr().setUnit( descr.getUnit() );
        return query;
    }

    public FunctionDescrBuilder newFunction() {
        FunctionDescrBuilder function = new FunctionDescrBuilderImpl( this );
        descr.addFunction( initDescr(function) );
        return function;
    }

    public AttributeDescrBuilder<PackageDescrBuilder> attribute( String name ) {
        AttributeDescrBuilder<PackageDescrBuilder> attribute = new AttributeDescrBuilderImpl<>( this, name );
        descr.addAttribute( initDescr(attribute) );
        return attribute;
    }

    private <T extends BaseDescr> T initDescr(DescrBuilder<PackageDescrBuilder, T> builder) {
        // resource for new descr already set in builder
        T descr = builder.getDescr();
        descr.setNamespace(this.descr.getNamespace());
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
