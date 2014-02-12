package org.drools.core.rule;

import org.drools.core.base.ClassObjectType;
import org.drools.core.beliefsystem.abductive.AbductedStatus;
import org.drools.core.spi.AcceptsClassObjectType;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class AbductiveQuery extends QueryImpl implements Externalizable, AcceptsClassObjectType {

    private ClassObjectType returnType;
    private transient Constructor constructor;

    private String[] params;
    private Object value;
    private boolean returnBound;

    public AbductiveQuery() {
        super();
    }

    public AbductiveQuery( String name, Object value ) {
        super( name );
        this.value = (value != null) ? value : AbductedStatus.ABDUCTED;
    }

    @Override
    public boolean isAbductive() {
        return true;
    }

    public void setReturnType( ClassObjectType objectType, String[] params ) throws NoSuchMethodException {
        this.returnType = objectType;
        this.params = params;
        findConstructor();
    }

    protected void findConstructor() throws NoSuchMethodException {
        int N = params.length - 1;

        constructor = null;
        List<Class> availableArgs = new ArrayList<Class>( N );
        for ( int j = 0; j < N; j++ ) {
            Declaration decl = getDeclaration( params[ j ] );
            if ( decl != null ) {
                availableArgs.add( decl.getExtractor().getExtractToClass() );
            }
        }
        Class klass = returnType.getClassType();
        while ( constructor == null ) {
            try {
                constructor = klass.getConstructor( availableArgs.toArray( new Class[ availableArgs.size() ] ) );
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

    @Override
    public void setClassObjectType( ClassObjectType classObjectType ) {
        returnType = classObjectType;
        if ( params != null ) { // the first time, params may not have been initialized yet
            try {
                findConstructor();
            } catch ( NoSuchMethodException e ) {
                e.printStackTrace();
                constructor = null;
                returnType = null;
            }
        }
    }

    public void setReturnBound( boolean returnBound ) {
        this.returnBound = returnBound;
    }

    public boolean isReturnBound() {
        return returnBound;
    }
}
