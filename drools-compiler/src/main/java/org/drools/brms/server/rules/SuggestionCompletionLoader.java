package org.drools.brms.server.rules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.codehaus.jfdi.interpreter.ClassTypeResolver;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.server.util.SuggestionCompletionEngineBuilder;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.ParserError;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.dsl.DSLMapping;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.resource.util.ByteArrayClassLoader;
import org.drools.util.asm.ClassFieldInspector;

/**
 * This utility class loads suggestion completion stuff for the package configuration, 
 * introspecting from models, templates etc. 
 * 
 * This also includes DSL stuff, basically, everything you need to get started with a package.
 * It also validates the package configuration, and can provide errors.
 * 
 * This does NOT validate assets in the package, other then to load up DSLs, models etc
 * as needed.
 * 
 * FYI: the tests for this are in the BRMS module, in context of a full BRMS.
 * 
 * @author Michael Neale
 *
 */
public class SuggestionCompletionLoader {

    private SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();
    private DrlParser                         parser  = new DrlParser();
    private final ByteArrayClassLoader        loader;
    protected List errors;

    public SuggestionCompletionLoader() {
        loader = new ByteArrayClassLoader( this.getClass().getClassLoader() );
    }

    /**
     * This will validate, and generate a new engine, ready to go.
     * If there are errors, you can get them by doing getErrors();
     * @param pkg
     * @return
     */
    public SuggestionCompletionEngine getSuggestionEngine(String header, List jars, List dsls) {
        errors = new ArrayList();
        builder.newCompletionEngine();

        

        if (!header.trim().equals( "" )) {
            processPackageHeader( header, jars );
        }

        // populating DSL sentences
        this.populateDSLSentences( dsls );
        
        return builder.getInstance();
    }

    private void processPackageHeader(String header, List jars) {
        // get fact types from imports
        PackageDescr pkgDescr;
        try {
            pkgDescr = parser.parse( header );
        } catch ( DroolsParserException e1 ) {
            throw new IllegalStateException( "Serious error, unable to validate package." );
        }
   
        if (this.parser.hasErrors()) {
            for ( Iterator iter = this.parser.getErrors().iterator(); iter.hasNext(); ) {
                ParserError element = (ParserError) iter.next();
                errors.add( element.getMessage() );
            }            
        }
        // populating information for the model itself
        this.populateModelInfo( pkgDescr, jars );
   
        // populating globals
        this.populateGlobalInfo( pkgDescr, jars );
   

    }

    /**
     * @param pkg
     * @param errors
     */
    private void populateDSLSentences(List dsls) {
//        AssetItemIterator it = pkg.listAssetsByFormat( new String[]{AssetFormats.DSL} );
//        while ( it.hasNext() ) {
//            AssetItem item = (AssetItem) it.next();
//            String dslData = item.getContent();
//            DSLMappingFile file = new DSLMappingFile();
//            try {
//                if ( file.parseAndLoad( new StringReader( dslData ) ) ) {
//                    DSLMapping mapping = file.getMapping();
//                    for ( Iterator entries = mapping.getEntries().iterator(); entries.hasNext(); ) {
//                        DSLMappingEntry entry = (DSLMappingEntry) entries.next();
//                        if (entry.getSection() == DSLMappingEntry.CONDITION) {
//                            builder.addDSLConditionSentence( entry.getMappingKey() );
//                        } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
//                            builder.addDSLActionSentence( entry.getMappingKey() );
//                        }
//                        
//                    }
//                } else {
//                    errors.add( file.getErrors().toString() );
//                }
//            } catch ( IOException e ) {
//                errors.add( "Error while loading DSL language configuration : " + item.getBinaryContentAttachmentFileName() + " error message: " + e.getMessage() );
//            }
//        }

        for ( Iterator it = dsls.iterator(); it.hasNext(); ) {
            DSLMappingFile file = (DSLMappingFile) it.next();        
            DSLMapping mapping = file.getMapping();
            for ( Iterator entries = mapping.getEntries().iterator(); entries.hasNext(); ) {
                DSLMappingEntry entry = (DSLMappingEntry) entries.next();
                if (entry.getSection() == DSLMappingEntry.CONDITION) {
                    builder.addDSLConditionSentence( entry.getMappingKey() );
                } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
                    builder.addDSLActionSentence( entry.getMappingKey() );
                }
            }
        }
     
        
    }

    /**
     * Populate the global stuff.
     */
    private void populateGlobalInfo(PackageDescr pkgDescr,
                                    List jars) {

        // populating information for the globals
        for ( Iterator it = pkgDescr.getGlobals().iterator(); it.hasNext(); ) {
            GlobalDescr global = (GlobalDescr) it.next();
            try {
                String shortTypeName = global.getType();
                if ( !this.builder.hasFieldsForType( shortTypeName ) ) {
                    Class clazz = loadClass( 
                                             global.getType(), jars );
                    loadClassFields( clazz,
                                     shortTypeName );

                    this.builder.addGlobalType( global.getIdentifier(),
                                                shortTypeName );
                }

                builder.addGlobalType( global.getIdentifier(),
                                       shortTypeName );
            } catch ( IOException e ) {
                errors.add( "Error while inspecting class for global: " + global.getType() + " error message: " + e.getMessage());
            }

        }
    }

    /**
     * Populate the fact type data.
     */
    private void populateModelInfo(PackageDescr pkgDescr, List jars) {

        // iterating over the import list
        ClassTypeResolver resolver = new ClassTypeResolver();
        for ( Iterator it = pkgDescr.getImports().iterator(); it.hasNext(); ) {
            ImportDescr imp = (ImportDescr) it.next();
            String classname = imp.getTarget();
            resolver.addImport( classname );

            Class clazz = loadClass(  classname, jars );
            if ( clazz != null ) {
                try {
                    String shortTypeName = getShortNameOfClass( clazz.getName() );
                    loadClassFields( clazz,
                                     shortTypeName );
                    builder.addFactType( shortTypeName );
                } catch ( IOException e ) {
                    errors.add( "Error while inspecting the class: " + classname + ". The error was: " + e.getMessage() );
                }
            }
        }

        // iterating over templates
        populateFactTemplateTypes( pkgDescr,
                                   resolver );
    }

    /**
     * Iterates over fact templates and add them to the model definition
     * 
     * @param pkgDescr
     */
    private void populateFactTemplateTypes(PackageDescr pkgDescr,
                                           ClassTypeResolver resolver) {
        for ( Iterator it = pkgDescr.getFactTemplates().iterator(); it.hasNext(); ) {
            FactTemplateDescr templ = (FactTemplateDescr) it.next();
            String factType = templ.getName();
            builder.addFactType( factType );

            String[] fields = new String[templ.getFields().size()];
            builder.addFieldsForType( factType,
                                      fields );

            int index = 0;
            for ( Iterator fieldsIt = templ.getFields().iterator(); fieldsIt.hasNext(); ) {
                FieldTemplateDescr fieldDescr = (FieldTemplateDescr) fieldsIt.next();
                fields[index++] = fieldDescr.getName();
                String fieldType = fieldDescr.getClassType();

                Class fieldTypeClass = null;
                try {
                    fieldTypeClass = resolver.resolveType( fieldType );
                } catch ( ClassNotFoundException e ) {
                    errors.add( "Fact template field type not found: " + fieldType );
                }
                builder.addFieldType( factType + "." + fieldDescr.getName(),
                                      getFieldType( fieldTypeClass ) );
            }
        }
    }

    private void loadClassFields(Class clazz,
                                 String shortTypeName) throws IOException {
        if (clazz == null) return;
        ClassFieldInspector inspector = new ClassFieldInspector( clazz );
        String[] fields = (String[]) inspector.getFieldNames().keySet().toArray( new String[inspector.getFieldNames().size()] );

        fields = removeIrrelevantFields( fields );

        builder.addFieldsForType( shortTypeName,
                                  fields );
        for ( int i = 0; i < fields.length; i++ ) {
            Class type = (Class) inspector.getFieldTypes().get( fields[i] );
            String fieldType = getFieldType( type );
            builder.addFieldType( shortTypeName + "." + fields[i],
                                  fieldType );
        }
    }

    String getShortNameOfClass(String clazz) {
        return clazz.substring( clazz.lastIndexOf( '.' ) + 1 );
    }

    /**
     * This will remove the unneeded "fields" that come from java.lang.Object
     * these are really not needed for the modeller.
     */
    String[] removeIrrelevantFields(String[] fields) {
        List result = new ArrayList();
        for ( int i = 0; i < fields.length; i++ ) {
            String field = fields[i];
            if ( field.equals( "class" ) || field.equals( "hashCode" ) || field.equals( "toString" ) ) {
                //ignore
            } else {
                result.add( field );
            }
        }
        return (String[]) result.toArray( new String[result.size()] );
    }

    /**
     * @param pkg
     * @param classname
     * @param clazz
     * @return
     */
    private Class loadClass(String classname, List jars) {
        Class clazz = null;
        try {
            // check if it is already in the classpath
            clazz = loader.loadClass( classname );

        } catch ( ClassNotFoundException e1 ) {

            // not found in the classpath, so check if it
            // is in a package model
            try {
                addJars( jars );
                clazz = loader.loadClass( classname );
            } catch ( IOException e ) {
                throw new IllegalStateException( e.getMessage() );
            } catch ( ClassNotFoundException e ) {
                errors.add( "Class not found: " + classname );
            }
        }
        return clazz;
    }
    
    /**
     * This will add the given jars to the classloader.
     */
    private void addJars(List jars) throws IOException {
        for ( Iterator it = jars.iterator(); it.hasNext(); ) {
            JarInputStream jis = (JarInputStream) it.next();
            JarEntry entry = null;
            byte[] buf = new byte[1024];
            int len = 0;
            while ( (entry = jis.getNextJarEntry()) != null ) {
                if ( !entry.isDirectory() ) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ( (len = jis.read( buf )) >= 0 ) {
                        out.write( buf, 0, len );
                    }
                    loader.addResource( entry.getName(),
                                        out.toByteArray() );
                }
            }        
            
        }
    }

    /**
     * @param inspector
     * @param fields
     * @param i
     * @return
     */
    private String getFieldType(Class type) {
        String fieldType = null; // if null, will use standard operators
        if ( type != null ) {
            if ( type.isPrimitive() && (type != boolean.class) ) {
                fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if ( Number.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if ( String.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_STRING;
            } else if ( Collection.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_COLLECTION;
            } else if ( Comparable.class.isAssignableFrom( type ) ) {
                fieldType = SuggestionCompletionEngine.TYPE_COMPARABLE;
            } 
        }
        return fieldType;
    }
    
    /**
     * @return true if there were errors when processing the package.
     */
    public boolean hasErrors() {
        return (this.errors.size() > 0);
    }
    
    /**
     * Returns a list of String errors.
     */
    public List getErrors() {
        return this.errors;
    }

}
