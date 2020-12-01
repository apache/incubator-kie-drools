/*
 * Copyright (c) 2020. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.rule.DialectRuntimeData;
import org.drools.core.rule.DialectRuntimeRegistry;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.spi.InternalReadAccessor;
import org.drools.core.spi.Wireable;
import org.drools.mvel.expr.MVELCompileable;
import org.mvel2.ParserConfiguration;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class MVELDialectRuntimeData
    implements
        DialectRuntimeData,
    Externalizable {

    private static final long              serialVersionUID = 510l;

    private final MapFunctionResolverFactory functionFactory = new MapFunctionResolverFactory();

    private Map<Wireable, List<MVELCompileable>>   invokerLookups = Collections.synchronizedMap( new IdentityHashMap<>() );
    private Set<MVELCompileable>             mvelReaders = ConcurrentHashMap.newKeySet();

    private ClassLoader                      rootClassLoader;
    private DialectRuntimeRegistry registry;

    private List<Wireable>                   wireList = Collections.emptyList();

    private Map<String, Object>              imports = new HashMap<String, Object>();
    private HashSet<String>                  packageImports = new HashSet<>();
    private ParserConfiguration              parserConfiguration;

    private boolean                          dirty;

    public void writeExternal(ObjectOutput out) throws IOException {
        for ( Entry<String, Object> entry : this.imports.entrySet() ) {
            // Field and Method are not serializable, so tokenise them
            if ( entry.getValue() instanceof Method ) {
                entry.setValue( "m:" + ((Method)entry.getValue()).getDeclaringClass().getName() );
            } else if ( entry.getValue() instanceof Field ) {
                entry.setValue( "f:" + ((Field)entry.getValue()).getDeclaringClass().getName() );
            }
        }
        out.writeObject( imports );
        out.writeObject( packageImports );

        out.writeObject( invokerLookups );
        out.writeObject( this.mvelReaders );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        imports = (Map) in.readObject();
        packageImports = (HashSet<String>) in.readObject();

        invokerLookups = (Map<Wireable, List<MVELCompileable>>) in.readObject();
        if ( !invokerLookups.isEmpty() ) {
            // we need a wireList for serialisation
            wireList = new ArrayList<Wireable>( invokerLookups.keySet() );
        }

        mvelReaders = ( Set<MVELCompileable> ) in.readObject();
    }


    public void merge( DialectRuntimeRegistry registry,
                           DialectRuntimeData newData ) {
        merge( registry, newData, false );
    }

    public void merge( DialectRuntimeRegistry registry,
                       DialectRuntimeData newData,
                       boolean excludeClasses ) {
        MVELDialectRuntimeData other = ( MVELDialectRuntimeData ) newData;

        for ( Entry<String, Object> entry : other.imports.entrySet() ) {
            if ( entry.getValue() instanceof Class ) {
                if ( !this.imports.containsKey( entry.getKey() ) ) {
                 // store it as a String, we'll re-resolve this later, against the correct ClassLoader
                    this.imports.put(  entry.getKey(), ((Class) entry.getValue()).getName() );
                }
            } else if ( entry.getValue() instanceof Method ) {
                this.imports.put( entry.getKey(), "m:" + ((Method)entry.getValue()).getDeclaringClass().getName() );
            } else {
                this.imports.put( entry.getKey(), entry.getValue() );
            }
        }

        this.packageImports.addAll( other.packageImports );
        for ( Entry<Wireable, List<MVELCompileable>> entry : other.invokerLookups.entrySet() ) {
            invokerLookups.put( entry.getKey(),
                                entry.getValue() );
            if ( this.wireList == Collections.<Wireable> emptyList() ) {
                this.wireList = new ArrayList<Wireable>();
            }
            wireList.add( entry.getKey() );
        }
        if ( this.mvelReaders == null ) {
            this.mvelReaders = new HashSet<MVELCompileable>();
        }
        this.mvelReaders.addAll( other.mvelReaders );
    }

    public DialectRuntimeData clone( DialectRuntimeRegistry registry,
                                     ClassLoader rootClassLoader ) {
        return clone( registry, rootClassLoader, false );
    }


    public DialectRuntimeData clone(DialectRuntimeRegistry registry,
                                    ClassLoader rootClassLoader,
                                    boolean excludeClasses ) {
        MVELDialectRuntimeData clone = new MVELDialectRuntimeData();
        clone.rootClassLoader = rootClassLoader;
        clone.merge( registry,
                     this,
                     excludeClasses );
        clone.onAdd( registry,
                     rootClassLoader );
        return clone;
    }

    public void onAdd(DialectRuntimeRegistry registry,
                      ClassLoader rootClassLoader) {
        this.rootClassLoader = rootClassLoader;
        this.registry = registry;
    }

    public void onRemove() {

    }

    public void onBeforeExecute() {
        for ( Wireable target : wireList ) {
            for (MVELCompileable compileable : invokerLookups.get( target )) {
                compileable.compile(this);

                // now wire up the target
                target.wire(compileable);
            }
        }
        wireList.clear();

        for ( MVELCompileable compileable : mvelReaders ) {
            compileable.compile( this );
        }

        if (dirty) {
            rewireImportedMethods();
            dirty = false;
        }
    }

    private void rewireImportedMethods() {
        if (imports != null) {
            Map<String, Object> rewiredMethod = new HashMap<String, Object>();
            for (Object imp : imports.values()) {
                if (imp instanceof Method) {
                    Method method = (Method)imp;
                    try {
                        Class<?> c = Class.forName(method.getDeclaringClass().getName(), false, getPackageClassLoader());
                        for (Method m : c.getDeclaredMethods()) {
                            if (method.getName().equals(m.getName()) && method.getParameterTypes().length == m.getParameterTypes().length) {
                                rewiredMethod.put(m.getName(), m);
                                break;
                            }
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            imports.putAll(rewiredMethod);
        }
    }

    public MapFunctionResolverFactory getFunctionFactory() {
        return this.functionFactory;
    }

    public void removeRule(KnowledgePackageImpl pkg,
                           RuleImpl rule) {
    }

    public void addFunction(org.mvel2.ast.Function function) {
        this.functionFactory.addFunction( function );
    }

    // TODO: FIXME: make it consistent with above
    public void removeFunction(KnowledgePackageImpl pkg,
                               org.drools.core.rule.Function function) {
        this.functionFactory.removeFunction( function.getName() );

    }

    public boolean isDirty() {
        return this.dirty;
    }

    public void setDirty( boolean dirty ) {
        this.dirty = dirty;
    }

    public void reload() {
    }

    public static class MapFunctionResolverFactory extends MapVariableResolverFactory
        implements
        Externalizable {

        public MapFunctionResolverFactory() {
            super( new HashMap<String, Object>() );
        }

        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeObject( this.variables );
        }

        public void readExternal(ObjectInput in) throws IOException,
                                                ClassNotFoundException {
            this.variables = (Map) in.readObject();
        }

        public void addFunction(org.mvel2.ast.Function function) {
            this.variables.put( function.getName(),
                                function );
        }

        public void removeFunction(String functionName) {
            this.variables.remove( functionName );
            this.variableResolvers.remove( functionName );
        }

        public VariableResolver createVariable(String name,
                                               Object value) {
            throw new RuntimeException( "variable is a read-only function pointer" );
        }

        public VariableResolver createIndexedVariable(int index,
                                                      String name,
                                                      Object value,
                                                      Class< ? > type) {
            throw new RuntimeException( "variable is a read-only function pointer" );
        }
    }

    public ParserConfiguration getParserConfiguration() {
        if ( parserConfiguration == null ) {
            ClassLoader packageClassLoader = getPackageClassLoader();

            String  key = null;
            Object value = null;
            try {
                // First replace fields and method tokens with actual instances
                for ( Entry<String, Object> entry : this.imports.entrySet() ) {
                    key = entry.getKey();
                    value = entry.getValue();
                    if ( entry.getValue() instanceof String ) {
                        String str = (String ) value;
                        // @TODO MVEL doesn't yet support importing of fields
                        if ( str.startsWith( "m:" ) ) {
                            Class cls = packageClassLoader.loadClass( str.substring( 2 ) );
                            for ( Method method : cls.getDeclaredMethods() ) {
                                if ( method.getName().equals( key ) ) {
                                    entry.setValue( method );
                                    break;
                                }
                            }
                        } else {
                            Class cls = packageClassLoader.loadClass( str);
                            entry.setValue( cls );
                        }
                    }
                }
            } catch ( ClassNotFoundException e ) {
                throw new IllegalArgumentException( "Unable to resolve method of field: " + key + " - " + value, e );

            }

            final ParserConfiguration conf = new ParserConfiguration();
            conf.setImports( this.imports );
            conf.setPackageImports( this.packageImports );
            conf.setClassLoader( packageClassLoader );
            this.parserConfiguration = conf;
        }
        return this.parserConfiguration;
    }

    @Override
    public void resetParserConfiguration() {
        this.parserConfiguration = null;
    }

    public void addImport(String str, Class cls) {
        this.imports.put( str, cls );
        if ( this.parserConfiguration != null ) {
            this.parserConfiguration.addImport( str,  cls );
        }
    }

    public void addImport(String str, Method method) {
        this.imports.put( str, method );
        if ( this.parserConfiguration != null ) {
            this.parserConfiguration.addImport( str,  method );
        }
    }

    public void addPackageImport(String str) {
        this.packageImports.add( str );
        if ( this.parserConfiguration != null ) {
            this.parserConfiguration.addPackageImport( str );
        }
    }

    public void addCompileable(MVELCompileable compilable) {
        this.mvelReaders.add(compilable);
    }

    public void compile(InternalReadAccessor reader) {
        addCompileable(( MVELCompileable ) reader);
        ((MVELCompileable) reader).compile(this);
    }

    public void addCompileable(Wireable wireable, MVELCompileable compilable) {
        invokerLookups.computeIfAbsent( wireable, k -> new ArrayList<MVELCompileable>() ).add( compilable );
    }

    public ClassLoader getRootClassLoader() {
        return rootClassLoader;
    }

    public ClassLoader getPackageClassLoader() {
        if (registry == null) {
            // should happens only in tests
            return getRootClassLoader();
        }
        JavaDialectRuntimeData javaRuntime = (JavaDialectRuntimeData) registry.getDialectData("java");
        return javaRuntime.getClassLoader();
    }

    public Map<String, Object> getImports() {
        return imports;
    }
}
