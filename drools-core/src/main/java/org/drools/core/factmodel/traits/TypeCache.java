package org.drools.core.factmodel.traits;

import org.drools.core.WorkingMemory;
import org.drools.core.reteoo.ReteooRuleBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeCache implements Externalizable {

    private Map<String,TypeWrapper> typeCache = new LinkedHashMap<String, TypeWrapper>();
    private boolean needsInit = false;

    public TypeCache( ) {
    }

    public boolean needsInit() {
        return needsInit;
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( typeCache.size() );
        List<String> keys = new ArrayList<String>( typeCache.keySet() );
        Collections.sort( keys );
        for ( String k : keys ) {
            out.writeObject( k );
            out.writeObject( typeCache.get( k ) );
        }
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        typeCache = new HashMap<String, TypeWrapper>();
        int n = in.readInt();
        for ( int j = 0; j < n; j++ ) {
            String k = (String) in.readObject();
            TypeWrapper tf = (TypeWrapper) in.readObject();
            typeCache.put( k, tf );
        }

        needsInit = true;
    }

    public TypeWrapper get( String name ) {
        return typeCache.get( name );
    }

    public void put( String name, TypeWrapper wrapper ) {
        typeCache.put( name, wrapper );
    }

    public void init( WorkingMemory wm ) {
        needsInit = false;
        ReteooRuleBase rb = (ReteooRuleBase) wm.getRuleBase();
        for ( TypeWrapper wrapper : typeCache.values() ) {
            if ( wrapper.getKlass() == null ) {
                try {
                    wrapper.setKlass( Class.forName( wrapper.getName(), false, rb.getRootClassLoader() ) );
                } catch ( ClassNotFoundException e ) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }
    }
}