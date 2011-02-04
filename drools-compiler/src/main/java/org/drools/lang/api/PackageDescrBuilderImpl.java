/**
 * 
 */
package org.drools.lang.api;

import org.drools.lang.descr.FunctionImportDescr;
import org.drools.lang.descr.PackageDescr;

/**
 * A builder implementation for PackageDescrs using a fluent API.
 */
public class PackageDescrBuilderImpl extends BaseDescrBuilderImpl<PackageDescr>
    implements
    PackageDescrBuilder {

    private PackageDescrBuilderImpl() {
        super( new PackageDescr() );
    }

    public static PackageDescrBuilder newPackage() {
        return new PackageDescrBuilderImpl();
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
        ImportDescrBuilder impl = new ImportDescrBuilderImpl( false );
        descr.addImport( impl.getDescr() );
        return impl;
    }

    public ImportDescrBuilder newFunctionImport() {
        ImportDescrBuilder impl = new ImportDescrBuilderImpl( true );
        descr.addFunctionImport( (FunctionImportDescr) impl.getDescr() );
        return impl;
    }

    public GlobalDescrBuilder newGlobal() {
        GlobalDescrBuilder global = new GlobalDescrBuilderImpl();
        descr.addGlobal( global.getDescr() );
        return global;
    }

    public DeclareDescrBuilder newDeclare() {
        DeclareDescrBuilder declare = new DeclareDescrBuilderImpl();
        descr.addTypeDeclaration( declare.getDescr() );
        return declare;
    }

    public RuleDescrBuilder newRule() {
        RuleDescrBuilder rule = new RuleDescrBuilderImpl();
        descr.addRule( rule.getDescr() );
        return rule;
    }

    public AttributeDescrBuilder attribute( String name ) {
        AttributeDescrBuilder attribute = new AttributeDescrBuilderImpl( name );
        descr.addAttribute( attribute.getDescr() );
        return attribute;
    }

    public FunctionDescrBuilder newFunction() {
        FunctionDescrBuilder function = new FunctionDescrBuilderImpl();
        descr.addFunction( function.getDescr() );
        return function;
    }

}
