package org.drools.lang.descr;

/**
 * This is the factory for ALL descriptors (eventually).
 * This will be tied in a with a package builder session, so it can add in context information for 
 * validation when parsing.
 * 
 * @author Michael Neale
 *
 */
public class DescrFactory {

    public PackageDescr createPackage(final String packageName) {
        return new PackageDescr( packageName );
    }

    public GlobalDescr createGlobal() {
        return new GlobalDescr();
    }

    public FromDescr createFrom() {
        return new FromDescr();
    }

    public AccumulateDescr createAccumulate() {
        return new AccumulateDescr();
    }

    public CollectDescr createCollect() {
        return new CollectDescr();
    }

    public ForallDescr createForall() {
        return new ForallDescr();
    }

    public ImportDescr createImport() {
        return new ImportDescr();
    }

    public FunctionImportDescr createFunctionImport() {
        return new FunctionImportDescr();
    }

    public ImportDescr createEventImport() {
        return new ImportDescr( true ); // import is an event
    }

    public QueryDescr createQuery(final String queryName) {
        return new QueryDescr( queryName,
                               "" );
    }

    public FunctionDescr createFunction(final String functionName,
                                        final String returnType) {
        return new FunctionDescr( functionName,
                                  returnType );
    }

    public FactTemplateDescr createFactTemplate(final String templateName) {
        return new FactTemplateDescr( templateName );
    }

    public FieldTemplateDescr createFieldTemplate() {
        return new FieldTemplateDescr();
    }

    public EntryPointDescr createEntryPoint() {
        return new EntryPointDescr();
    }
    
    public TypeDeclarationDescr createTypeDeclaration() {
        return new TypeDeclarationDescr();
    }    
}
