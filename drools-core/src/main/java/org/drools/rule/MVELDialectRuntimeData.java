/*
 * Copyright 2010 JBoss Inc
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

package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.drools.RuntimeDroolsException;
import org.drools.base.mvel.MVELCompileable;
import static org.drools.base.mvel.MVELCompilationUnit.loadClass;
import org.drools.spi.Wireable;
import org.drools.util.CompositeClassLoader;
import org.mvel2.ParserConfiguration;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class MVELDialectRuntimeData
    implements
    DialectRuntimeData,
    Externalizable {
    private MapFunctionResolverFactory     functionFactory;

    private Map<Wireable, MVELCompileable> invokerLookups;
    private List<MVELCompileable>          mvelReaders;

    private CompositeClassLoader           rootClassLoader;

    private List<Wireable>                 wireList = Collections.<Wireable> emptyList();
    
    private Map<String, Object>           imports;
    private HashSet<String>               packageImports;
    private ParserConfiguration           parserConfiguration;

    public MVELDialectRuntimeData() {
        this.functionFactory = new MapFunctionResolverFactory();
        this.invokerLookups = new IdentityHashMap<Wireable, MVELCompileable>();
        this.mvelReaders = new ArrayList<MVELCompileable> ();
        this.imports = new HashMap();
        this.packageImports = new HashSet();        
    }

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
        packageImports = (HashSet) in.readObject();
        
        invokerLookups = (Map<Wireable, MVELCompileable>) in.readObject();
        if ( !invokerLookups.isEmpty() ) {
            // we need a wireList for serialisation
            wireList = new ArrayList<Wireable>( invokerLookups.keySet() );
        }
        
        mvelReaders = ( List<MVELCompileable> ) in.readObject();
    }

    public void merge(DialectRuntimeRegistry registry,
                      DialectRuntimeData newData) {
        MVELDialectRuntimeData other = (MVELDialectRuntimeData) newData;
        this.imports.putAll( other.imports );
        this.packageImports.addAll( other.packageImports );
        for ( Entry<Wireable, MVELCompileable> entry : other.invokerLookups.entrySet() ) {
            invokerLookups.put( entry.getKey(),
                                entry.getValue() );

            if ( this.wireList == Collections.<Wireable> emptyList() ) {
                this.wireList = new ArrayList<Wireable>();
            }
            wireList.add( entry.getKey() );
            //            // first make sure the MVELCompilationUnit is compiled            
            //            MVELCompilable component = entry.getValue();
            //            component.compile( rootClassLoader );
            //            
            //            // now wire up the target
            //            Wireable target = entry.getKey();
            //            target.wire( component );
            //            System.out.println( component );
        }
        this.mvelReaders = new ArrayList<MVELCompileable>();
        this.mvelReaders.addAll( other.mvelReaders );
    }

    public DialectRuntimeData clone(DialectRuntimeRegistry registry,
                                    CompositeClassLoader rootClassLoader) {
        DialectRuntimeData clone = new MVELDialectRuntimeData();
        clone.merge( registry,
                     this );
        clone.onAdd( registry,
                     rootClassLoader );
        return clone;
    }

    public void onAdd(DialectRuntimeRegistry registry,
                      CompositeClassLoader rootClassLoader) {
        this.rootClassLoader = rootClassLoader;

        //        for (Entry<Wireable, MVELCompilable> entry : this.invokerLookups.entrySet() ) {
        //            // first make sure the MVELCompilationUnit is compiled            
        //            MVELCompilable component = entry.getValue();
        //            component.compile( rootClassLoader );
        //            
        //            // now wire up the target
        //            Wireable target = entry.getKey();
        //            target.wire( component );
        //        }
    }

    public void onRemove() {

    }

    public void onBeforeExecute() {
        for ( Wireable target : wireList ) {
            MVELCompileable compileable = invokerLookups.get( target );
            compileable.compile( this );

            // now wire up the target
            target.wire( compileable );
        }
        wireList.clear();
        
        for ( MVELCompileable compileable : mvelReaders ) {
            compileable.compile( this );
        }
    }

    public MapFunctionResolverFactory getFunctionFactory() {
        return this.functionFactory;
    }

    public void removeRule(Package pkg,
                           Rule rule) {
    }

    public void addFunction(org.mvel2.ast.Function function) {
        this.functionFactory.addFunction( function );
    }

    // TODO: FIXME: make it consistent with above
    public void removeFunction(Package pkg,
                               org.drools.rule.Function function) {
        this.functionFactory.removeFunction( function.getName() );

    }

    public boolean isDirty() {
        return false;
    }

    public void setDirty(boolean dirty) {
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
            ClassLoader classLoader = rootClassLoader;
            
            {
                String  key = null;
                Object value = null;
                try {
                    // First replace fields and method tokens with actual instances
                    for ( Entry<String, Object> entry : this.imports.entrySet() ) {
                        key = entry.getKey();
                        value = entry.getValue();
                        if ( entry.getValue() instanceof String ) {
                            String str = (String ) value;                 
                            Class cls = this.rootClassLoader.loadClass( str.substring( 2 ) );
                            if ( str.startsWith( "m:" ) ) {
                                String methodName =  key;
                                for ( Method method : cls.getDeclaredMethods() ) {
                                    if ( method.getName().equals( methodName ) ) {
                                        entry.setValue( method );
                                        continue;
                                    }
                                }                        
                            } else {
                                // @TODO MVEL doesn't yet support importing of fields
//                                String fieldName = key;                    
//                                for ( Field field : cls.getFields() ) {
//                                    if ( field.isAccessible() && field.getName().equals( fieldName ) ) {
//                                        entry.setValue( field );
//                                        continue;
//                                    }
//                                }                        
                            }
                        }
                    }
                } catch ( ClassNotFoundException e ) {
                    throw new IllegalArgumentException( "Unable to resolve method of field: " + key + " - " + value, e );
                
                }
            }
    
            this.parserConfiguration = new ParserConfiguration();
            this.parserConfiguration.setImports( this.imports );
            this.parserConfiguration.setPackageImports( this.packageImports );
            this.parserConfiguration.setClassLoader( classLoader );  
        }
        return this.parserConfiguration;
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
    
    public void addImport(String str, Field field) {
//        this.imports.put( str, field );
//        if ( this.parserConfiguration != null ) {
//            this.parserConfiguration.addImport( str,  field );
//        }
    }
    
    public void addPackageImport(String str) {
        this.packageImports.add( str );
        if ( this.parserConfiguration != null ) {
            this.parserConfiguration.addPackageImport( str );
        }
    }
    
    public void addCompileable(MVELCompileable compilable) {
        this.mvelReaders.add( compilable );
    }
    
    public void addCompileable(Wireable wireable,
                               MVELCompileable compilable) {
        invokerLookups.put( wireable,
                            compilable );
    }

    public Map<Wireable, MVELCompileable> getLookup() {
        return this.invokerLookups;
    }

    public CompositeClassLoader getRootClassLoader() {
        return rootClassLoader;
    }
    
    
}
