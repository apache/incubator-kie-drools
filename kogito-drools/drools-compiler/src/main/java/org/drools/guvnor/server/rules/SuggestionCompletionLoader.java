package org.drools.guvnor.server.rules;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import org.drools.base.ClassTypeResolver;
import org.drools.compiler.DrlParser;
import org.drools.compiler.DroolsParserException;
import org.drools.compiler.ParserError;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.server.util.ClassMethodInspector;
import org.drools.guvnor.server.util.DataEnumLoader;
import org.drools.guvnor.server.util.SuggestionCompletionEngineBuilder;
import org.drools.lang.descr.FactTemplateDescr;
import org.drools.lang.descr.FieldTemplateDescr;
import org.drools.lang.descr.GlobalDescr;
import org.drools.lang.descr.ImportDescr;
import org.drools.lang.descr.PackageDescr;
import org.drools.lang.descr.TypeDeclarationDescr;
import org.drools.lang.descr.TypeFieldDescr;
import org.drools.lang.dsl.AbstractDSLMappingEntry;
import org.drools.lang.dsl.DSLMapping;
import org.drools.lang.dsl.DSLMappingEntry;
import org.drools.lang.dsl.DSLMappingFile;
import org.drools.lang.dsl.DSLTokenizedMappingFile;
import org.drools.rule.MapBackedClassLoader;
import org.drools.util.asm.ClassFieldInspector;

/**
 * This utility class loads suggestion completion stuff for the package
 * configuration, introspecting from models, templates etc.
 * <p/>
 * This also includes DSL stuff, basically, everything you need to get started
 * with a package. It also validates the package configuration, and can provide
 * errors.
 * <p/>
 * This does NOT validate assets in the package, other then to load up DSLs,
 * models etc as needed.
 * <p/>
 * FYI: the tests for this are in the BRMS module, in context of a full BRMS.
 *
 * @author Michael Neale
 */
public class SuggestionCompletionLoader {

    private final SuggestionCompletionEngineBuilder builder = new SuggestionCompletionEngineBuilder();

    private final DrlParser parser = new DrlParser();

    private final MapBackedClassLoader loader;

    protected List<String> errors = new ArrayList<String>();

    // iterating over the import list
    final ClassTypeResolver resolver;

    /**
     * This uses the current classes classloader as a base, and jars can be
     * added.
     */
    public SuggestionCompletionLoader() {
        this(null);
    }

    /**
     * This allows a pre existing classloader to be used (and preferred) for
     * resolving types.
     */
    public SuggestionCompletionLoader(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = Thread.currentThread().getContextClassLoader();
            if (classLoader == null) {
                classLoader = this.getClass().getClassLoader();
            }
        }
        this.loader = new MapBackedClassLoader(classLoader);
        this.resolver = new ClassTypeResolver(new HashSet(),
                this.loader);
    }

    /**
     * This will validate, and generate a new engine, ready to go. If there are
     * errors, you can get them by doing getErrors();
     *
     * @param header The package configuration file content.
     * @param jars   a list of jars to look inside (pass in empty array if not
     *               needed) this is a list of {@link JarInputStream}
     * @param dsls   any dsl files. This is a list of {@link DSLMappingFile}.
     * @return A SuggestionCompletionEngine ready to be used in anger.
     */
    public SuggestionCompletionEngine getSuggestionEngine(final String header,
                                                          final List jars,
                                                          final List dsls) {
        return this.getSuggestionEngine(header, jars, dsls, Collections.EMPTY_LIST);
    }


    /**
     * This will validate, and generate a new engine, ready to go. If there are
     * errors, you can get them by doing getErrors();
     *
     * @param header    The package configuration file content.
     * @param jars      a list of jars to look inside (pass in empty array if not
     *                  needed) this is a list of {@link JarInputStream}
     * @param dsls      any dsl files. This is a list of {@link DSLMappingFile}.
     * @param dataEnums this is a list of String's which hold data enum definitions.
     *                  (normally will be just one, but for completeness can load multiple).
     * @return A SuggestionCompletionEngine ready to be used in anger.
     */
    public SuggestionCompletionEngine getSuggestionEngine(final String header,
                                                          final List jars,
                                                          final List dsls,
                                                          final List dataEnums) {
        this.builder.newCompletionEngine();

        if (!header.trim().equals("")) {
            processPackageHeader(header,
                    jars);
        }

        // populating DSL sentences
        this.populateDSLSentences(dsls);


        SuggestionCompletionEngine sce = this.builder.getInstance();

        populateDateEnums(dataEnums, sce);

        Arrays.sort(sce.factTypes);

        return sce;
    }

    private void populateDateEnums(List dataEnums, SuggestionCompletionEngine sce) {
        for (Iterator iter = dataEnums.iterator(); iter.hasNext();) {
            String enumFile = (String) iter.next();
            DataEnumLoader enumLoader = new DataEnumLoader(enumFile);
            if (enumLoader.hasErrors()) {
                this.errors.addAll(enumLoader.getErrors());
            } else {
                sce.dataEnumLists.putAll(enumLoader.getData());
            }
        }

    }

    private void processPackageHeader(final String header,
                                      final List jars) {
        // get fact types from imports
        PackageDescr pkgDescr;
        try {
            pkgDescr = this.parser.parse(header);
        } catch (final DroolsParserException e1) {
            throw new IllegalStateException("Serious error, unable to validate package.");
        }

        if (this.parser.hasErrors()) {
            for (final Iterator iter = this.parser.getErrors().iterator(); iter.hasNext();) {
                final ParserError element = (ParserError) iter.next();
                this.errors.add(element.getMessage());
            }
        }

        if (pkgDescr != null) { //only if no errors

            // populating information for the model itself
            this.populateModelInfo(pkgDescr,
                    jars);

            // populating globals
            this.populateGlobalInfo(pkgDescr,
                    jars);
        }

    }

    /**
     * @param pkg
     * @param errors
     */
    private void populateDSLSentences(final List dsls) {
        // AssetItemIterator it = pkg.listAssetsByFormat( new
        // String[]{AssetFormats.DSL} );
        // while ( it.hasNext() ) {
        // AssetItem item = (AssetItem) it.next();
        // String dslData = item.getContent();
        // DSLMappingFile file = new DSLMappingFile();
        // try {
        // if ( file.parseAndLoad( new StringReader( dslData ) ) ) {
        // DSLMapping mapping = file.getMapping();
        // for ( Iterator entries = mapping.getEntries().iterator();
        // entries.hasNext(); ) {
        // DSLMappingEntry entry = (DSLMappingEntry) entries.next();
        // if (entry.getSection() == DSLMappingEntry.CONDITION) {
        // builder.addDSLConditionSentence( entry.getMappingKey() );
        // } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
        // builder.addDSLActionSentence( entry.getMappingKey() );
        // }
        //
        // }
        // } else {
        // errors.add( file.getErrors().toString() );
        // }
        // } catch ( IOException e ) {
        // errors.add( "Error while loading DSL language configuration : " +
        // item.getBinaryContentAttachmentFileName() + " error message: " +
        // e.getMessage() );
        // }
        // }

        for (final Iterator it = dsls.iterator(); it.hasNext();) {
            final DSLTokenizedMappingFile file = (DSLTokenizedMappingFile) it.next();
            final DSLMapping mapping = file.getMapping();
            for (final Iterator entries = mapping.getEntries().iterator(); entries.hasNext();) {
                final AbstractDSLMappingEntry entry = (AbstractDSLMappingEntry) entries.next();
                if (entry.getSection() == DSLMappingEntry.CONDITION) {
                    this.builder.addDSLConditionSentence(entry.getMappingKey());
                } else if (entry.getSection() == DSLMappingEntry.CONSEQUENCE) {
                    this.builder.addDSLActionSentence(entry.getMappingKey());
                } else if (entry.getSection() == DSLMappingEntry.KEYWORD) {
                    this.builder.addDSLMapping(entry);
                } else if (entry.getSection() == DSLMappingEntry.ANY) {

                }
            }
        }

    }

    /**
     * Populate the global stuff.
     */
    private void populateGlobalInfo(final PackageDescr pkgDescr,
                                    final List jars) {

        // populating information for the globals
        for (final Iterator it = pkgDescr.getGlobals().iterator(); it.hasNext();) {
            final GlobalDescr global = (GlobalDescr) it.next();
            try {
                final String shortTypeName = getShortNameOfClass(global.getType());
                if (!this.builder.hasFieldsForType(shortTypeName)) {
                    final Class clazz = loadClass(global.getType(),
                            jars);
                    loadClassFields(clazz,
                            shortTypeName);

                    this.builder.addGlobalType(global.getIdentifier(),
                            shortTypeName);
                    if (clazz != null && Collection.class.isAssignableFrom(clazz)) {
                        this.builder.addGlobalCollection(global.getIdentifier());
                    }
                }

                this.builder.addGlobalType(global.getIdentifier(),
                        shortTypeName);
            } catch (final IOException e) {
                this.errors.add("Error while inspecting class for global: " + global.getType() + " error message: " + e.getMessage());
            }

        }
    }

    /**
     * Populate the fact type data.
     */
    private void populateModelInfo(final PackageDescr pkgDescr,
                                   final List jars) {
        for (final Iterator it = pkgDescr.getImports().iterator(); it.hasNext();) {
            final ImportDescr imp = (ImportDescr) it.next();
            final String className = imp.getTarget();
            if (className.endsWith("*")) {
                this.errors.add("Unable to introspect model for wild card imports (" + className + "). Please explicitly import each fact type you require.");
            } else {
                resolver.addImport(className);
                final Class clazz = loadClass(className, jars);
                if (clazz != null) {
                    try {
                        final String shortTypeName = getShortNameOfClass(clazz.getName());
                        loadClassFields(clazz, shortTypeName);
                        this.builder.addFactType(shortTypeName);
                    } catch (final IOException e) {
                        this.errors.add("Error while inspecting the class: " + className + ". The error was: " + e.getMessage());
                    } catch (NoClassDefFoundError e) {
                        this.errors.add("Unable to find the class: " + e.getMessage().replace('/', '.') + " which is required by: " + className + ". You may need to add more classes to the model.");
                    }
                }
            }
        }

        /** now we do the dynamic facts - the declared types */
        Set<String> declaredTypes = new HashSet<String>();

        for (final Iterator<TypeDeclarationDescr> it = pkgDescr.getTypeDeclarations().iterator(); it.hasNext();) {
            TypeDeclarationDescr td = it.next();

            if (td.getFields().size() > 0) {
                //add the type to the map
                String declaredType = td.getTypeName();
                declaredTypes.add(declaredType);
                this.builder.addFactType(declaredType);
                List<String> fieldNames = new ArrayList<String>();
                for (Map.Entry<String, TypeFieldDescr> f : td.getFields().entrySet()) {
                    String fieldName = f.getKey();
                    fieldNames.add(fieldName);
                    String fieldClass = f.getValue().getPattern().getObjectType();


                    if (declaredTypes.contains(fieldClass)) {
                        this.builder.addFieldType(declaredType + "." + fieldName, fieldClass);//SuggestionCompletionEngine.TYPE_OBJECT );
                    } else {
                        try {
                            Class clz = resolver.resolveType(fieldClass);
                            this.builder.addFieldType(declaredType + "." + fieldName, getFieldType(clz));
                        } catch (ClassNotFoundException e) {
                            this.errors.add("Class of field not found: " + fieldClass);
                        }
                    }

                }

                this.builder.addFieldsForType(declaredType, fieldNames.toArray(new String[fieldNames.size()]));

            }
        }

        // iterating over templates
        populateFactTemplateTypes(pkgDescr,
                resolver);
    }

    /**
     * Iterates over fact templates and add them to the model definition
     *
     * @param pkgDescr
     */
    private void populateFactTemplateTypes(final PackageDescr pkgDescr,
                                           final ClassTypeResolver resolver) {
        for (final Iterator it = pkgDescr.getFactTemplates().iterator(); it.hasNext();) {
            final FactTemplateDescr templ = (FactTemplateDescr) it.next();
            final String factType = templ.getName();
            this.builder.addFactType(factType);

            final String[] fields = new String[templ.getFields().size()];


            int index = 0;
            for (final Iterator fieldsIt = templ.getFields().iterator(); fieldsIt.hasNext();) {
                final FieldTemplateDescr fieldDescr = (FieldTemplateDescr) fieldsIt.next();
                fields[index++] = fieldDescr.getName();
                final String fieldType = fieldDescr.getClassType();

                Class fieldTypeClass = null;
                try {
                    fieldTypeClass = resolver.resolveType(fieldType);
                } catch (final ClassNotFoundException e) {
                    this.errors.add("Fact template field type not found: " + fieldType);
                }
                this.builder.addFieldType(factType + "." + fieldDescr.getName(),
                        getFieldType(fieldTypeClass));
            }

            Arrays.sort(fields);
            this.builder.addFieldsForType(factType,
                    fields);

        }
    }

    private Class loadClass(String className,
                            List jars) {
        Class clazz = null;
        try {
            clazz = resolver.resolveType(className);
        } catch (ClassNotFoundException e1) {
            try {
                addJars(jars);
                clazz = resolver.resolveType(className);
            } catch (Exception e) {
                this.errors.add("Class not found: " + className);
            }
        }
        return clazz;
    }

    private void loadClassFields(final Class clazz,
                                 final String shortTypeName) throws IOException {
        if (clazz == null) {
            return;
        }

        final ClassFieldInspector inspector = new ClassFieldInspector(clazz);
        Set<String> fieldSet = new HashSet<String>();
        fieldSet.addAll(inspector.getFieldNames().keySet());
        // add the "this" field. This won't come out from the inspector
        fieldSet.add("this");

        String [] fields = fieldSet.toArray(new String []{});
        Arrays.sort(fields);

        fields = removeIrrelevantFields(fields);

        this.builder.addFieldsForType(shortTypeName, fields);
        
        Method[] methods = clazz.getMethods();
        List<String> modifierStrings = new ArrayList<String>();
        for (Method method : methods) {
            modifierStrings.add(method.getName());
        }
        String[] modifiers = new String[modifierStrings.size()];
        modifierStrings.toArray(modifiers);

        this.builder.addModifiersForType(shortTypeName, modifiers);

        // remove this back out because there is no type for it. We add it explicity
        fieldSet.remove("this");
        this.builder.addFieldType(shortTypeName + ".this", SuggestionCompletionEngine.TYPE_OBJECT);

        for (String field : fieldSet) {
            final Class type = inspector.getFieldTypes().get(field);
            final String fieldType = getFieldType(type);
            this.builder.addFieldType(shortTypeName + "." + field, fieldType);
        }
        
        ClassMethodInspector methodInspector = new ClassMethodInspector(clazz);
        
        this.builder.getInstance().addMethodInfo( shortTypeName, methodInspector.getMethodInfos() );
    }

    String getShortNameOfClass(final String clazz) {
        return clazz.substring(clazz.lastIndexOf('.') + 1);
    }

    /**
     * This will remove the unneeded "fields" that come from java.lang.Object
     * these are really not needed for the modeller.
     */
    String[] removeIrrelevantFields(final String[] fields) {
        final List result = new ArrayList();
        for (int i = 0; i < fields.length; i++) {
            final String field = fields[i];
            if (field.equals("class") || field.equals("hashCode") || field.equals("toString")) {
                // ignore
            } else {
                result.add(field);
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    /**
     * This will add the given jars to the classloader.
     */
    private void addJars(final List jars) throws IOException {
        for (final Iterator it = jars.iterator(); it.hasNext();) {
            final JarInputStream jis = (JarInputStream) it.next();
            JarEntry entry  ;
            final byte[] buf = new byte[1024];
            int len  ;
            while ((entry = jis.getNextJarEntry()) != null) {
                if (!entry.isDirectory() && entry.getName().endsWith( ".class" )) {
                    final ByteArrayOutputStream out = new ByteArrayOutputStream();
                    while ((len = jis.read(buf)) >= 0) {
                        out.write(buf,
                                0,
                                len);
                    }
                    this.loader.addResource(entry.getName(),
                            out.toByteArray());
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
    private String getFieldType(final Class type) {
        String fieldType = null; // if null, will use standard operators
        if (type != null) {
            if (type.isPrimitive() && (type != boolean.class)) {
                fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if (Number.class.isAssignableFrom(type)) {
                fieldType = SuggestionCompletionEngine.TYPE_NUMERIC;
            } else if (String.class.isAssignableFrom(type)) {
                fieldType = SuggestionCompletionEngine.TYPE_STRING;
            } else if (Collection.class.isAssignableFrom(type)) {
                fieldType = SuggestionCompletionEngine.TYPE_COLLECTION;
            } else if (Boolean.class.isAssignableFrom(type) || boolean.class.isAssignableFrom(type)) {
                fieldType = SuggestionCompletionEngine.TYPE_BOOLEAN;
            } else if ( Date.class.isAssignableFrom( type )) {
                fieldType = SuggestionCompletionEngine.TYPE_DATE; // MN: wait until we support it.
            } else if (Comparable.class.isAssignableFrom(type)) {
                fieldType = SuggestionCompletionEngine.TYPE_COMPARABLE;
            }
            else {
                try {
                    Class clazz = resolver.resolveType(type.getName());
                    fieldType = clazz.getSimpleName();
                } catch (ClassNotFoundException e) {
                    fieldType = SuggestionCompletionEngine.TYPE_OBJECT;
                }
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
    public List<String> getErrors() {
        return this.errors;
    }

}
