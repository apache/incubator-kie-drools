package org.drools.rule;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.drools.base.mvel.MVELCompileable;
import org.drools.spi.Wireable;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.MapVariableResolverFactory;

public class MVELDialectRuntimeData
    implements
    DialectRuntimeData,
    Externalizable {
    private MapFunctionResolverFactory     functionFactory;

    private Map<Wireable, MVELCompileable> invokerLookups;

    private CompositeClassLoader           rootClassLoader;

    private List<Wireable>                 wireList = Collections.<Wireable> emptyList();

    public MVELDialectRuntimeData() {
        this.functionFactory = new MapFunctionResolverFactory();
        invokerLookups = new IdentityHashMap<Wireable, MVELCompileable>();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( invokerLookups );
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        invokerLookups = (Map<Wireable, MVELCompileable>) in.readObject();
        if ( !invokerLookups.isEmpty() ) {
            // we need a wireList for serialisation
            wireList = new ArrayList<Wireable>( invokerLookups.keySet() );          
        }
    }

    public void merge(DialectRuntimeRegistry registry,
                      DialectRuntimeData newData) {
        MVELDialectRuntimeData other = (MVELDialectRuntimeData) newData;
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
            compileable.compile( rootClassLoader );

            // now wire up the target
            target.wire( compileable );
        }
        wireList.clear();
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

    public void addCompileable(Wireable wireable,
                              MVELCompileable compilable) {
//        if ( this.wireList == Collections.<Wireable> emptyList() ) {
//            this.wireList = new ArrayList<Wireable>();
//        }
//        wireList.add( wireable );
        invokerLookups.put( wireable,
                            compilable );
    }

    public Map<Wireable, MVELCompileable> getLookup() {
        return this.invokerLookups;
    }
}
