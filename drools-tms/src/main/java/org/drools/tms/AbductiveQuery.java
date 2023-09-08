/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.tms;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import org.drools.base.base.DroolsQuery;
import org.drools.base.base.ValueResolver;
import org.drools.base.base.ClassObjectType;
import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.ObjectStore;
import org.drools.core.common.TruthMaintenanceSystemFactory;
import org.drools.base.rule.Declaration;
import org.drools.base.definitions.rule.impl.QueryImpl;
import org.drools.base.base.AcceptsClassObjectType;
import org.drools.core.rule.consequence.InternalMatch;
import org.drools.base.base.ObjectType;
import org.drools.tms.beliefsystem.BeliefSet;
import org.drools.tms.beliefsystem.abductive.Abductive;
import org.kie.api.runtime.rule.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AbductiveQuery extends QueryImpl implements Externalizable, AcceptsClassObjectType {

    private static final Logger LOG = LoggerFactory.getLogger(AbductiveQuery.class);

    private ObjectType returnType;
    private transient Constructor cachedConstructor;

    private String[] params;
    private String[] abducibleArgs;
    private int[] arg2param;
    private Object value;
    private boolean returnBound;

    public AbductiveQuery() {
        super();
    }

    public AbductiveQuery( String name ) {
        super( name );
    }

    @Override
    public boolean isAbductive() {
        return true;
    }

    @Override
    public void setReturnType( ObjectType objectType, String[] params, String[] args, Declaration[] declarations ) throws NoSuchMethodException {
        this.returnType = objectType;
        this.params = params;
        if ( args != null ) {
            this.abducibleArgs = Arrays.copyOf( args, args.length );
            this.arg2param = new int[ abducibleArgs.length ];
            for ( int j = 0; j < this.abducibleArgs.length; j++ ) {
                boolean matched = false;
                for ( int k = 0; k < params.length; k++ ) {
                    if ( abducibleArgs[ j ].equals( params[ k ] ) ) {
                        this.arg2param[ j ] = k;
                        matched = true;
                        break;
                    }
                }
                if ( ! matched ) {
                    throw new IllegalArgumentException( "Constructor argument " + abducibleArgs[ j ] + " cannot be resolved " );
                }
            }
        } else {
            this.abducibleArgs = Arrays.copyOf( params, params.length - 1 );
            this.arg2param = new int[ abducibleArgs.length ];
            for ( int j = 0; j < this.abducibleArgs.length; j++ ) {
                this.arg2param[ j ] = j;
            }
        }

        findConstructor( declarations );
    }

    private void findConstructor( Declaration[] declarations ) throws NoSuchMethodException {
        int argsLength = this.abducibleArgs.length;

        cachedConstructor = null;
        List<Class> availableArgs = argsLength > 0 ? new ArrayList<>(argsLength) : Collections.emptyList();
        for ( int j = 0; j < argsLength; j++ ) {
            // during the initial build (KieBuilder), the declarations are provided on the fly and use for type checking
            // when building the KieBase, the internal declarations are set and can be used
            Declaration decl = declarations != null ? declarations[ arg2param[ j ] ] : getDeclaration( abducibleArgs[ j ] );
            if ( decl != null ) {
                availableArgs.add( decl.getDeclarationClass() );
            }
        }
        Class klass = ((ClassObjectType) returnType).getClassType();
        while ( cachedConstructor == null ) {
            try {
                cachedConstructor = klass.getConstructor( availableArgs.toArray( new Class[ availableArgs.size() ] ) );
            } catch ( NoSuchMethodException nsme ) {
                if ( klass == Object.class ) {
                    throw nsme;
                } else {
                    klass = klass.getSuperclass();
                }
            }
        }
    }

    @Override
    public void writeExternal( ObjectOutput out ) throws IOException {
        super.writeExternal( out );
        out.writeObject( returnType );
        out.writeObject( params );
        out.writeObject( value );
        out.writeBoolean( returnBound );
    }

    @Override
    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        super.readExternal( in );
        returnType = (ClassObjectType) in.readObject();
        params = (String[]) in.readObject();
        value = in.readObject();
        returnBound = in.readBoolean();
    }

    @Override
    public void setClassObjectType( ClassObjectType classObjectType ) {
        returnType = classObjectType;
        if ( params != null ) { // the first time, params may not have been initialized yet
            getConstructor();
        }
    }

    private Constructor getConstructor() {
        if (cachedConstructor == null || cachedConstructor.getDeclaringClass() != ((ClassObjectType) returnType).getClassType()) {
            try {
                findConstructor( null );
            } catch ( NoSuchMethodException e ) {
                LOG.error("Exception", e);
                cachedConstructor = null;
                returnType = null;
            }
        }
        return cachedConstructor;
    }

    @Override
    public boolean isReturnBound() {
        return returnBound;
    }

    @Override
    public boolean processAbduction(Match resultLeftTuple, DroolsQuery dquery, Object[] objects, ValueResolver valueResolver) {
        boolean pass = true;
        InternalWorkingMemory workingMemory = (InternalWorkingMemory) valueResolver;
        int numArgs = abducibleArgs.length;
        Object[] constructorArgs = new Object[ abducibleArgs.length ];
        for ( int j = 0; j < numArgs; j++ ) {
            int k = arg2param[ j ];
            if ( objects[ k ] != null ) {
                constructorArgs[ j ] = objects[ k ];
            } else if ( dquery.getElements()[ k ] != null ) {
                constructorArgs[ j ] = dquery.getElements()[ k ];
            }
        }
        Object abduced = abduce( constructorArgs );
        if ( abduced != null ) {
            boolean firstAssertion = true;
            ObjectStore store = workingMemory.getObjectStore();
            InternalFactHandle handle = store.getHandleForObject( abduced );
            if ( handle != null ) {
                abduced = handle.getObject();
                firstAssertion = false;
            } else {
                handle = TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(workingMemory).insertPositive( abduced, (InternalMatch) resultLeftTuple);
            }
            BeliefSet bs = handle.getEqualityKey() != null ? ((TruthMaintenanceSystemEqualityKey)handle.getEqualityKey()).getBeliefSet() : null;
            if ( bs == null ) {
                abduced = handle.getObject();
            } else {
                if ( ! bs.isPositive() ) {
                    pass = false;
                } else {
                    if ( !firstAssertion ) {
                        TruthMaintenanceSystemFactory.get().getOrCreateTruthMaintenanceSystem(workingMemory).insertPositive( abduced, (InternalMatch) resultLeftTuple);
                    }
                }
            }
        }
        objects[ objects.length - 1 ] = abduced;
        return pass;
    }

    private Object abduce( Object... args ) {
        Constructor constructor = getConstructor();
        if ( constructor == null ) {
            // no proper constructor was found
            return null;
        }
        try {
            for ( int j = 0; j < args.length; j++ ) {
                if ( args[ j ] == null && constructor.getParameterTypes()[ j ].isPrimitive() ) {
                    args[ j ] = boolean.class == constructor.getParameterTypes()[ j ] ? false : 0;
                }
            }
            return constructor.newInstance( args );
        } catch ( Exception e ) {
            LOG.error("Exception", e);
            return null;
        }
    }

    @Override
    public Class<? extends Annotation> getAbductiveAnnotationClass() {
        return Abductive.class;
    }

    @Override
    public <T extends Annotation> Class<?> getAbductionClass(Function<Class<T>, T> annotationReader) {
        return ((Abductive)annotationReader.apply((Class<T>) Abductive.class)).target();
    }
}
