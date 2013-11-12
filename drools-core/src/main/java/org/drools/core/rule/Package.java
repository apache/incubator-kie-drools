/*
 * Copyright 2005 JBoss Inc
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

package org.drools.core.rule;

import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.base.ClassFieldAccessorStore;
import org.drools.core.base.TypeResolver;
import org.drools.core.common.DroolsObjectInputStream;
import org.drools.core.common.DroolsObjectOutputStream;
import org.drools.core.common.ProjectClassLoader;
import org.drools.core.factmodel.traits.TraitRegistry;
import org.drools.core.facttemplates.FactTemplate;
import org.drools.core.util.ClassUtils;
import org.kie.api.definition.process.Process;
import org.kie.api.definition.type.FactType;
import org.kie.api.definition.type.Role;
import org.kie.api.io.Resource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Collection of related <code>Rule</code>s.
 *
 * @see Rule
 *
 *
 * @version $Id: Package.java,v 1.1 2005/07/26 01:06:31 mproctor Exp $
 */
public class Package
                    implements
                    Externalizable {

    // ------------------------------------------------------------
    // Constants`
    // ------------------------------------------------------------

    private static final long              serialVersionUID = 510l;

    // ------------------------------------------------------------
    // Instance members
    // ------------------------------------------------------------

    /** Name of the pkg. */
    private String                         name;

    /** Set of all rule-names in this <code>Package</code>. */
    private Map<String, Rule>              rules;

    private Map<String, ImportDeclaration> imports;

    private Map<String, Function>          functions;

    private Set<String>                    staticImports;

    private Map<String, String>            globals;

    private Map<String, FactTemplate>      factTemplates;

    private Map<String, Process>           ruleFlows;

    // private JavaDialectData packageCompilationData;
    private DialectRuntimeRegistry         dialectRuntimeRegistry;

    private LinkedHashMap<String, TypeDeclaration>
                                           typeDeclarations;

    private Set<String>                    entryPointsIds   = Collections.emptySet();

    private Map<String, WindowDeclaration> windowDeclarations;

    private ClassFieldAccessorStore        classFieldAccessorStore;

    private TraitRegistry                  traitRegistry;

    /**
     * This is to indicate the the package has no errors during the
     * compilation/building phase
     */
    private boolean                        valid            = true;

    private boolean                        needStreamMode   = false;

    /**
     * This will keep a summary error message as to why this package is not
     * valid
     */
    private String                         errorSummary;

    private transient TypeResolver         typeResolver;

    private transient AtomicBoolean        inUse            = new AtomicBoolean(false);

    // ------------------------------------------------------------
    // Constructors
    // ------------------------------------------------------------

    /**
     * Default constructor - for Externalizable. This should never be used by a
     * user, as it will result in an invalid state for the instance.
     */
    public Package() {
        this(null);
    }

    /**
     * Construct.
     *
     * @param name
     *            The name of this <code>Package</code>.
     */
    public Package(final String name) {
        this.name = name;
        this.imports = new HashMap<String, ImportDeclaration>();
        this.typeDeclarations = new LinkedHashMap<String, TypeDeclaration>();
        this.staticImports = Collections.EMPTY_SET;
        this.rules = new LinkedHashMap<String, Rule>();
        this.ruleFlows = Collections.emptyMap();
        this.globals = Collections.emptyMap();
        this.factTemplates = Collections.emptyMap();
        this.functions = Collections.emptyMap();
        this.dialectRuntimeRegistry = new DialectRuntimeRegistry();
        this.classFieldAccessorStore = new ClassFieldAccessorStore();
        this.entryPointsIds = Collections.emptySet();
        this.windowDeclarations = Collections.emptyMap();
    }

    /**
     * Handles the write serialization of the Package. Patterns in Rules may
     * reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the
     * generated bytecode. The generated bytecode must be restored before any
     * Rules.
     *
     * @param stream
     *            out the stream to write the object to; should be an instance
     *            of DroolsObjectOutputStream or OutputStream
     */
    public void writeExternal( ObjectOutput stream ) throws IOException {
        boolean isDroolsStream = stream instanceof DroolsObjectOutputStream;
        ByteArrayOutputStream bytes = null;
        ObjectOutput out;

        if (isDroolsStream) {
            out = stream;
        } else {
            bytes = new ByteArrayOutputStream();
            out = new DroolsObjectOutputStream( bytes );
        }
        out.writeObject( this.dialectRuntimeRegistry );
        out.writeObject( this.typeDeclarations );
        out.writeObject( this.name );
        out.writeObject( this.imports );
        out.writeObject( this.staticImports );
        out.writeObject( this.functions );
        out.writeObject( this.factTemplates );
        out.writeObject( this.ruleFlows );
        out.writeObject( this.globals );
        out.writeBoolean( this.valid );
        out.writeBoolean( this.needStreamMode );
        out.writeObject( this.rules );
        out.writeObject( this.classFieldAccessorStore );
        out.writeObject( this.entryPointsIds );
        out.writeObject( this.windowDeclarations );
        out.writeObject( this.traitRegistry );
        // writing the whole stream as a byte array
        if (!isDroolsStream) {
            bytes.flush();
            bytes.close();
            stream.writeObject( bytes.toByteArray() );
        }
    }

    /**
     * Handles the read serialization of the Package. Patterns in Rules may
     * reference generated data which cannot be serialized by default methods.
     * The Package uses PackageCompilationData to hold a reference to the
     * generated bytecode; which must be restored before any Rules. A custom
     * ObjectInputStream, able to resolve classes against the bytecode in the
     * PackageCompilationData, is used to restore the Rules.
     *
     * @param stream,
     *            the stream to read data from in order to restore the object;
     *            should be an instance of DroolsObjectInputStream or
     *            InputStream
     */
    public void readExternal( ObjectInput stream ) throws IOException,
            ClassNotFoundException {
        boolean isDroolsStream = stream instanceof DroolsObjectInputStream;
        DroolsObjectInputStream in = isDroolsStream ? (DroolsObjectInputStream) stream
                                                   : new DroolsObjectInputStream(
                                                                                  new ByteArrayInputStream(
                                                                                                            (byte[]) stream.readObject() ) );

        // setting parent classloader for dialect datas
        this.dialectRuntimeRegistry = (DialectRuntimeRegistry) in.readObject();

        this.typeDeclarations = (LinkedHashMap) in.readObject();
        this.name = (String) in.readObject();
        this.imports = (Map<String, ImportDeclaration>) in.readObject();
        this.staticImports = (Set) in.readObject();
        this.functions = (Map<String, Function>) in.readObject();
        this.factTemplates = (Map) in.readObject();
        this.ruleFlows = (Map) in.readObject();
        this.globals = (Map<String, String>) in.readObject();
        this.valid = in.readBoolean();
        this.needStreamMode = in.readBoolean();
        this.rules = (Map<String, Rule>) in.readObject();
        this.classFieldAccessorStore = (ClassFieldAccessorStore) in.readObject();
        this.entryPointsIds = (Set<String>) in.readObject();
        this.windowDeclarations = (Map<String, WindowDeclaration>) in.readObject();
        this.traitRegistry = (TraitRegistry) in.readObject();
        if (!isDroolsStream) {
            in.close();
        }
    }

    // ------------------------------------------------------------
    // Instance methods
    // ------------------------------------------------------------

    /**
     * Retrieve the name of this <code>Package</code>.
     *
     * @return The name of this <code>Package</code>.
     */
    public String getName() {
        return this.name;
    }

    public ClassLoader getPackageClassLoader() {
        JavaDialectRuntimeData javaRuntime = (JavaDialectRuntimeData) getDialectRuntimeRegistry().getDialectData( "java" );
        return javaRuntime.getClassLoader();
    }

    public DialectRuntimeRegistry getDialectRuntimeRegistry() {
        return this.dialectRuntimeRegistry;
    }

    public void setDialectRuntimeRegistry(DialectRuntimeRegistry dialectRuntimeRegistry) {
        this.dialectRuntimeRegistry = dialectRuntimeRegistry;
    }

    public void addImport( final ImportDeclaration importDecl ) {
        this.imports.put( importDecl.getTarget(),
                          importDecl );
    }

    public void removeImport( final String importEntry ) {
        this.imports.remove( importEntry );
    }

    public Map<String, ImportDeclaration> getImports() {
        return this.imports;
    }

    public void addTypeDeclaration( final TypeDeclaration typeDecl ) {
        this.typeDeclarations.put( typeDecl.getTypeName(),
                                   typeDecl );
    }

    public void removeTypeDeclaration( final String type ) {
        this.typeDeclarations.remove( type );
    }

    public Map<String, TypeDeclaration> getTypeDeclarations() {
        return this.typeDeclarations;
    }

    public TypeDeclaration getTypeDeclaration( Class<?> clazz ) {
        if (clazz == null) {
            return null;
        }
        TypeDeclaration typeDeclaration = getTypeDeclaration(clazz.getSimpleName());
        if (typeDeclaration == null) {
            // check if clazz is resolved by any of the type declarations
            for ( TypeDeclaration type : this.typeDeclarations.values() ) {
                if ( type.matches( clazz ) ) {
                    typeDeclaration = type;
                    break;
                }
            }
        }
        return typeDeclaration;
    }

    public TypeDeclaration getTypeDeclaration( String type ) {
        return this.typeDeclarations.get( type );
    }

    public void addStaticImport( final String functionImport ) {
        if (this.staticImports == Collections.EMPTY_SET) {
            this.staticImports = new HashSet<String>( 2 );
        }
        this.staticImports.add( functionImport );
    }

    public void addFunction( final Function function ) {
        if (this.functions == Collections.EMPTY_MAP) {
            this.functions = new HashMap<String, Function>( 1 );
        }

        this.functions.put( function.getName(),
                            function );
        dialectRuntimeRegistry.getDialectData( function.getDialect() ).setDirty( true );
    }

    public Map<String, Function> getFunctions() {
        return this.functions;
    }

    public void removeFunctionImport( final String functionImport ) {
        this.staticImports.remove( functionImport );
    }

    public Set<String> getStaticImports() {
        return this.staticImports;
    }

    public void addGlobal( final String identifier,
            final Class<?> clazz ) {
        if (this.globals == Collections.EMPTY_MAP) {
            this.globals = new HashMap<String, String>( 1 );
        }
        this.globals.put( identifier,
                          clazz.getName() );
    }

    public void removeGlobal( final String identifier ) {
        this.globals.remove( identifier );
    }

    public Map<String, String> getGlobals() {
        return this.globals;
    }

    public void removeFunction( final String functionName ) {
        Function function = this.functions.remove( functionName );
        if (function != null) {
            this.dialectRuntimeRegistry.removeFunction( this,
                                                        function );
        }
    }

    public FactTemplate getFactTemplate( final String name ) {
        return this.factTemplates.get( name );
    }

    public void addFactTemplate( final FactTemplate factTemplate ) {
        if (this.factTemplates == Collections.EMPTY_MAP) {
            this.factTemplates = new HashMap( 1 );
        }
        this.factTemplates.put( factTemplate.getName(),
                                factTemplate );
    }

    /**
     * Add a <code>Rule</code> to this <code>Package</code>.
     *
     * @param rule
     *            The rule to add.
     *
     * @throws DuplicateRuleNameException
     *             If the <code>Rule</code> attempting to be added has the
     *             same name as another previously added <code>Rule</code>.
     * @throws InvalidRuleException
     *             If the <code>Rule</code> is not valid.
     */
    public void addRule( final Rule rule ) {
        this.rules.put( rule.getName(),
                        rule );
    }

    /**
     * Add a rule flow to this package.
     */
    public void addProcess( Process process ) {
        if (this.ruleFlows == Collections.EMPTY_MAP) {
            this.ruleFlows = new HashMap<String, Process>();
        }
        this.ruleFlows.put( process.getId(),
                            process );
    }

    /**
     * Get the rule flows for this package. The key is the ruleflow id. It will
     * be Collections.EMPTY_MAP if none have been added.
     */
    public Map<String, Process> getRuleFlows() {
        return this.ruleFlows;
    }

    /**
     * Rule flows can be removed by ID.
     */
    public void removeRuleFlow( String id ) {
        if (!this.ruleFlows.containsKey( id )) {
            throw new IllegalArgumentException( "The rule flow with id [" + id + "] is not part of this package." );
        }
        this.ruleFlows.remove( id );
    }

    public void removeRule( final Rule rule ) {
        this.rules.remove( rule.getName() );
        this.dialectRuntimeRegistry.removeRule( this,
                                                rule );
    }

    /**
     * Retrieve a <code>Rule</code> by name.
     *
     * @param name
     *            The name of the <code>Rule</code> to retrieve.
     *
     * @return The named <code>Rule</code>, or <code>null</code> if not
     *         such <code>Rule</code> has been added to this
     *         <code>Package</code>.
     */
    public Rule getRule( final String name ) {
        return this.rules.get( name );
    }

    /**
     * Retrieve all <code>Rules</code> in this <code>Package</code>.
     *
     * @return An array of all <code>Rules</code> in this <code>Package</code>.
     */
    public Rule[] getRules() {
        return this.rules.values().toArray( new Rule[this.rules.size()] );
    }

    // public JavaDialectData getPackageCompilationData() {
    // return this.packageCompilationData;
    // }

    public String toString() {
        return "[Package name=" + this.name + "]";
    }

    /** Once this is called, the package will be marked as invalid */
    public void setError( final String summary ) {
        this.errorSummary = summary;
        this.valid = false;
    }

    /** Once this is called, the package will be marked as invalid */
    public void resetErrors() {
        this.errorSummary = "";
        this.valid = true;
    }

    /**
     * @return true (default) if there are no build/structural problems.
     */
    public boolean isValid() {
        return this.valid;
    }

    /** This will throw an exception if the package is not valid */
    public void checkValidity() {
        if (!isValid()) {
            throw new InvalidRulePackage( this.getErrorSummary() );
        }
    }

    /**
     * This will return the error summary (if any) if the package is invalid.
     */
    public String getErrorSummary() {
        return this.errorSummary;
    }

    public boolean equals( final Object object ) {
        if (this == object) {
            return true;
        }

        if (object == null || !( object instanceof Package )) {
            return false;
        }

        final Package other = (Package) object;

        return ( this.name.equals( other.name ) );
    }

    public int hashCode() {
        return this.name.hashCode();
    }

    /**
     * Returns true if clazz is imported as an Event class in this package
     *
     * @param clazz
     * @return true if clazz is imported as an Event class in this package
     */
    public boolean isEvent( Class clazz ) {
        TypeDeclaration typeDeclaration = getTypeDeclaration(clazz);
        if (typeDeclaration != null) {
            return typeDeclaration.getRole() == TypeDeclaration.Role.EVENT;
        }
        Role role = (Role) clazz.getAnnotation( Role.class );
        return role != null && role.value() == Role.Type.EVENT;
    }

    public void clear() {
        this.rules.clear();
        this.dialectRuntimeRegistry.clear();
        this.ruleFlows.clear();
        this.imports.clear();
        this.functions.clear();
        this.staticImports.clear();
        this.globals.clear();
        this.factTemplates.clear();
        this.typeDeclarations.clear();
        this.windowDeclarations.clear();
    }

    public FactType getFactType( final String typeName ) {
        if (typeName == null || ( this.name != null && !typeName.startsWith( this.name + "." ) )) {
            return null;
        }
        // in case the package name is != null, remove the package name from the
        // beginning of the type name
        String key = this.name == null ? typeName : typeName.substring( this.name.length() + 1 );
        TypeDeclaration decl = this.typeDeclarations.get( key );
        return decl != null ? decl.getTypeClassDef() : null;
    }

    public Map<String, FactType> getFactTypes() {
        Map<String, FactType> types = new HashMap<String, FactType>();
        for ( String typeName : typeDeclarations.keySet() ) {
            types.put( typeName, typeDeclarations.get( typeName ).getTypeClassDef() );
        }
        return types;
    }

    public ClassFieldAccessorStore getClassFieldAccessorStore() {
        return classFieldAccessorStore;
    }

    public void setClassFieldAccessorCache( ClassFieldAccessorCache classFieldAccessorCache ) {
        this.classFieldAccessorStore.setClassFieldAccessorCache( classFieldAccessorCache );
    }

    public Set<String> getEntryPointIds() {
        return entryPointsIds;
    }

    public void addEntryPointId( String id ) {
        if (entryPointsIds == Collections.EMPTY_SET) {
            entryPointsIds = new HashSet<String>();
        }
        entryPointsIds.add( id );
    }

    public TypeResolver getTypeResolver() {
        return typeResolver;
    }

    public void setTypeResolver( TypeResolver typeResolver ) {
        this.typeResolver = typeResolver;
    }

    public void addWindowDeclaration( WindowDeclaration window ) {
        if( windowDeclarations == Collections.EMPTY_MAP ) {
            windowDeclarations = new HashMap<String, WindowDeclaration>();
        }
        this.windowDeclarations.put( window.getName(), window );
    }

    public Map<String, WindowDeclaration> getWindowDeclarations() {
        return windowDeclarations;
    }

    public void setWindowDeclarations( Map<String, WindowDeclaration> windowDeclarations ) {
        this.windowDeclarations = windowDeclarations;
    }

    public boolean hasTraitRegistry() {
        return traitRegistry != null;
    }

    public TraitRegistry getTraitRegistry() {
        if ( traitRegistry == null ) {
            traitRegistry = new TraitRegistry();
        }
        return traitRegistry;
    }

    public boolean removeObjectsGeneratedFromResource(Resource resource) {
        List<Rule> rulesToBeRemoved = removeRulesGeneratedFromResource(resource);

        List<TypeDeclaration> typesToBeRemoved = getTypesGeneratedFromResource(resource);
        if (!typesToBeRemoved.isEmpty()) {
            JavaDialectRuntimeData dialect = (JavaDialectRuntimeData) getDialectRuntimeRegistry().getDialectData( "java" );
            for (TypeDeclaration type : typesToBeRemoved) {
                classFieldAccessorStore.removeType(type);
                dialect.remove(type.getTypeClassName());
                removeTypeDeclaration(type.getTypeName());
            }
            dialect.reload();
        }

        List<Function> functionsToBeRemoved = removeFunctionsGeneratedFromResource(resource);

        return !rulesToBeRemoved.isEmpty() || !typesToBeRemoved.isEmpty() || !functionsToBeRemoved.isEmpty();
    }

    public List<Rule> removeRulesGeneratedFromResource(Resource resource) {
        List<Rule> rulesToBeRemoved = getRulesGeneratedFromResource(resource);
        for (Rule rule : rulesToBeRemoved) {
            removeRule(rule);
        }
        return rulesToBeRemoved;
    }

    public List<Rule> getRulesGeneratedFromResource(Resource resource) {
        List<Rule> rulesToBeRemoved = new ArrayList<Rule>();
        for (Rule rule : rules.values()) {
            if (resource.equals(rule.getResource())) {
                rulesToBeRemoved.add(rule);
            }
        }
        return rulesToBeRemoved;
    }

    public List<TypeDeclaration> getTypesGeneratedFromResource(Resource resource) {
        List<TypeDeclaration> typesToBeRemoved = new ArrayList<TypeDeclaration>();
        for (TypeDeclaration type : typeDeclarations.values()) {
            if (resource.equals(type.getResource())) {
                typesToBeRemoved.add(type);
            }
        }
        return typesToBeRemoved;
    }

    public List<Function> removeFunctionsGeneratedFromResource(Resource resource) {
        List<Function> functionsToBeRemoved = getFunctionsGeneratedFromResource(resource);
        for (Function function : functionsToBeRemoved) {
            removeFunction(function.getName());
        }
        return functionsToBeRemoved;
    }

    public List<Function> getFunctionsGeneratedFromResource(Resource resource) {
        List<Function> functionsToBeRemoved = new ArrayList<Function>();
        for (Function function : functions.values()) {
            if (resource.equals(function.getResource())) {
                functionsToBeRemoved.add(function);
            }
        }
        return functionsToBeRemoved;
    }

    public boolean needsStreamMode() {
        return needStreamMode;
    }

    public void setNeedStreamMode() {
        this.needStreamMode = true;
    }

    public Package deepCloneIfAlreadyInUse(ClassLoader classLoader) {
        if (inUse.compareAndSet(false, true)) {
            return this;
        }

        if (classLoader instanceof ProjectClassLoader) {
            ClassLoader originalClassLoader = ((JavaDialectRuntimeData)dialectRuntimeRegistry.getDialectData("java")).getRootClassLoader();
            if (originalClassLoader instanceof ProjectClassLoader) {
                ((ProjectClassLoader)classLoader).initFrom((ProjectClassLoader)originalClassLoader);
            }
        }

        return ClassUtils.deepClone(this, classLoader);
    }
}
