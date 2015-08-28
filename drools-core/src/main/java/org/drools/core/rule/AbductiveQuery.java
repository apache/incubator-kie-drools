/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
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

import org.drools.core.base.ClassObjectType;
import org.drools.core.spi.AcceptsClassObjectType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class AbductiveQuery extends QueryImpl implements Externalizable, AcceptsClassObjectType {

    private ClassObjectType returnType;
    private transient Constructor cachedConstructor;

    private String[] params;
    private String[] abducibleArgs;
    private int[] arg2param;
    private Object value;
    private boolean returnBound;

    public AbductiveQuery() {
        super();
    }

    public AbductiveQuery( String name, Object value ) {
        super( name );
    }

    @Override
    public boolean isAbductive() {
        return true;
    }

    public void setReturnType( ClassObjectType objectType, String[] params, String[] args, Declaration[] declarations ) throws NoSuchMethodException, IllegalArgumentException {
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
                    if ( matched ) {
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

    protected void findConstructor( Declaration[] declarations ) throws NoSuchMethodException {
        int N = this.abducibleArgs.length;

        cachedConstructor = null;
        List<Class> availableArgs = N > 0 ? new ArrayList<Class>( N ) : Collections.<Class>emptyList();
        for ( int j = 0; j < N; j++ ) {
            // during the initial build (KieBuilder), the declarations are provided on the fly and use for type checking
            // when building the KieBase, the internal declarations are set and can be used
            Declaration decl = declarations != null ? declarations[ mapArgToParam( j ) ] : getDeclaration( abducibleArgs[ j ] );
            if ( decl != null ) {
                availableArgs.add( decl.getDeclarationClass() );
            }
        }
        Class klass = returnType.getClassType();
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

    public Object abduce( Object... args ) {
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
            e.printStackTrace();
            return null;
        }
    }

    public ClassObjectType getReturnType() {
        return returnType;
    }

    public Object getValue() {
        return value;
    }

    public String[] getAbducibleArgs() {
        return abducibleArgs;
    }

    @Override
    public void setClassObjectType( ClassObjectType classObjectType ) {
        returnType = classObjectType;
        if ( params != null ) { // the first time, params may not have been initialized yet
            getConstructor();
        }
    }

    private Constructor getConstructor() {
        if (cachedConstructor == null || cachedConstructor.getDeclaringClass() != returnType.getClassType()) {
            try {
                findConstructor( null );
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
                cachedConstructor = null;
                returnType = null;
            }
        }
        return cachedConstructor;
    }

    public void setReturnBound( boolean returnBound ) {
        this.returnBound = returnBound;
    }

    public boolean isReturnBound() {
        return returnBound;
    }

    public int mapArgToParam( int j ) {
        return this.arg2param[ j ];
    }
}
