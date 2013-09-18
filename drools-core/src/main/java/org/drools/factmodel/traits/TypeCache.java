package org.drools.factmodel.traits;

import org.drools.WorkingMemory;
import org.drools.reteoo.ReteooRuleBase;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
        out.writeObject( typeCache );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        typeCache = (Map<String, TypeWrapper>) in.readObject();
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