package org.drools.lang.api.impl;

import org.drools.io.Resource;
import org.drools.lang.api.AttributeDescrBuilder;
import org.drools.lang.api.DeclareDescrBuilder;
import org.drools.lang.api.FunctionDescrBuilder;
import org.drools.lang.api.GlobalDescrBuilder;
import org.drools.lang.api.ImportDescrBuilder;
import org.drools.lang.api.PackageDescrBuilder;
import org.drools.lang.api.QueryDescrBuilder;
import org.drools.lang.api.RuleDescrBuilder;
import org.drools.lang.descr.AttributeDescr;
import org.drools.lang.descr.FunctionDescr;
import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.QueryDescr;
import org.drools.lang.descr.RuleDescr;

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
        ImportDescr importDescr = impl.getDescr();
        importDescr.setResource( resource );
        descr.addImport( importDescr );
        return impl;
    }

    public ImportDescrBuilder newFunctionImport() {
        ImportDescrBuilder impl = new ImportDescrBuilderImpl( this, true );
        FunctionImportDescr importDescr = (FunctionImportDescr) impl.getDescr();
        importDescr.setResource( resource );
        descr.addFunctionImport( importDescr );
        return impl;
    }

    public GlobalDescrBuilder newGlobal() {
        GlobalDescrBuilder global = new GlobalDescrBuilderImpl( this );
        GlobalDescr globalDescr = global.getDescr();
        globalDescr.setResource( resource );
        descr.addGlobal( globalDescr );
        return global;
    }

    public DeclareDescrBuilder newDeclare() {
        DeclareDescrBuilder declare = new DeclareDescrBuilderImpl( this );
        return declare;
    }

    public RuleDescrBuilder newRule() {
        RuleDescrBuilder rule = new RuleDescrBuilderImpl( this );
        RuleDescr ruleDescr = rule.getDescr();
        ruleDescr.setResource( resource );
        descr.addRule( ruleDescr );
        return rule;
    }

    public QueryDescrBuilder newQuery() {
        QueryDescrBuilder query = new QueryDescrBuilderImpl( this );
        QueryDescr queryDescr = query.getDescr();
        queryDescr.setResource( resource );
        descr.addRule( queryDescr );
        return query;
    }

    public FunctionDescrBuilder newFunction() {
        FunctionDescrBuilder function = new FunctionDescrBuilderImpl( this );
        FunctionDescr functionDescr = function.getDescr();
        descr.addFunction( functionDescr );
        functionDescr.setResource( resource );
        return function;
    }

    public AttributeDescrBuilder<PackageDescrBuilder> attribute( String name ) {
        AttributeDescrBuilder<PackageDescrBuilder> attribute = new AttributeDescrBuilderImpl<PackageDescrBuilder>( this, name );
        AttributeDescr attributeDescr = attribute.getDescr();
        attributeDescr.setResource( resource );
        descr.addAttribute( attributeDescr );
        return attribute;
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
