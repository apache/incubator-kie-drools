/**
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

package org.drools.base.mvel;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.drools.WorkingMemory;
import org.mvel2.integration.VariableResolver;
import org.mvel2.integration.impl.BaseVariableResolverFactory;
import org.mvel2.integration.impl.LocalVariableResolverFactory;
import org.mvel2.integration.impl.StaticMethodImportResolverFactory;

public class DroolsProcessMVELFactory extends BaseVariableResolverFactory
    implements
    LocalVariableResolverFactory,
    DroolsGlobalVariableMVELFactory,
    DroolsLocalVariableMVELFactory,    
    Externalizable,
    Cloneable {

    private static final long serialVersionUID = 400L;

    /**
     * Holds the instance of the variables.
     */

    private Map               globals;

    private WorkingMemory     workingMemory;

    private Map               localVariables;

    public DroolsProcessMVELFactory() {

    }

    public DroolsProcessMVELFactory(final Map globals) {
        this( globals,
              null );
    }

    public DroolsProcessMVELFactory(final Map globals,
                             final List[] externals) {
        this.globals = globals;

        if ( externals != null && MVELDebugHandler.isDebugMode() ) {
            for ( int i = 0; i < externals.length; i++ ) {
                for ( Iterator it = externals[i].iterator(); it.hasNext(); ) {
                    String identifier = (String) it.next();
                    isResolveable( identifier );
                }
            }
        }
    }

    public void readExternal(ObjectInput in) throws IOException,
                                            ClassNotFoundException {
        globals = (Map) in.readObject();
        workingMemory = (WorkingMemory) in.readObject();
        localVariables = (Map) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject( globals );
        out.writeObject( workingMemory );
        out.writeObject( localVariables );
    }

    public static void addStaticImport(StaticMethodImportResolverFactory factory,
                                       String staticImportEntry,
                                       ClassLoader classLoader) {
        int index = staticImportEntry.lastIndexOf( '.' );
        String className = staticImportEntry.substring( 0,
                                                        index );
        String methodName = staticImportEntry.substring( index + 1 );

        try {
            Class cls = classLoader.loadClass( className );
            Method[] methods = cls.getDeclaredMethods();
            for ( int i = 0; i < methods.length; i++ ) {
                if ( methods[i].getName().equals( methodName ) ) {
                    factory.createVariable( methodName,
                                            methods[i] );
                    break;
                }
            }
        } catch ( ClassNotFoundException e ) {
            throw new RuntimeException( "Unable to dynamically load method '" + staticImportEntry + "'" );
        }

    }

    public Map getVariableResolvers() {
        return this.variableResolvers;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

    public void setContext(final WorkingMemory workingMemory) {
        this.workingMemory = workingMemory;
    }

    public Object getValue(final String identifier) {
        return this.workingMemory.getGlobal( identifier );
    }

    public Object getLocalValue(final String identifier) {
        return this.localVariables.get( identifier );
    }

    public void setLocalValue(final String identifier,
                              final Object value) {
        if ( this.localVariables == null ) {
            this.localVariables = new HashMap();
        }
        this.localVariables.put( identifier,
                                 value );
    }

    public VariableResolver createVariable(String name,
                                           Object value) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr == null ) {
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name ) );
        }

        vr.setValue( value );
        return vr;
    }

    public VariableResolver createVariable(String name,
                                           Object value,
                                           Class type) {
        VariableResolver vr = getVariableResolver( name );
        if ( vr == null ) {
            addResolver( name,
                         vr = new LocalVariableResolver( this,
                                                         name,
                                                         type ) );
        }

        vr.setValue( value );
        return vr;
    }

    public boolean isResolveable(String name) {
        if ( this.variableResolvers != null && this.variableResolvers.containsKey( name ) ) {
            return true;
        } else if ( this.workingMemory.getGlobal( name ) != null ) {
            addResolver( name,
                         new DroolsMVELGlobalVariable( name,
                                                       (Class) this.globals.get( name ),
                                                       this ) );
            return true;
        } else if ( nextFactory != null ) {
            return nextFactory.isResolveable( name );
        }

        return false;
    }

    private void addResolver(String name,
                             VariableResolver vr) {
        if ( this.variableResolvers == null ) {
            this.variableResolvers = new HashMap();
        }
        this.variableResolvers.put( name,
                                    vr );
    }

    public boolean isTarget(String name) {
        if ( this.variableResolvers != null ) {
            return this.variableResolvers.containsKey( name );
        } else {
            return false;
        }
    }

    public Object clone() {
        return new DroolsProcessMVELFactory( this.globals );
    }

    /**
     * @return the globals
     */
    protected Map getGlobals() {
        return globals;
    }

    /**
     * @return the localVariables
     */
    protected Map getLocalVariables() {
        return localVariables;
    }
}
