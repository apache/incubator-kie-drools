package org.drools.lang.api.impl;

import org.drools.io.Resource;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.DescrBuilder;
import org.drools.lang.api.FunctionDescrBuilder;
import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.QueryDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.BaseDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.PackageDescr;

/**
 * A builder implementation for PackageDescrs using a fluent API.
 */
public class PackageDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescrBuilder, PackageDescr>
    implements
    PackageDescrBuilder {

    private Resource resource;

    private PackageDescrBuilderImpl() {
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
