package org.drools.core.factmodel.traits;

import org.drools.core.WorkingMemory;
import org.drools.core.util.ClassUtils;
import org.mvel2.MVEL;

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

public class TraitFieldTMSImpl implements TraitFieldTMS, Externalizable {

    private Map<String, TraitField> fieldTMS = new LinkedHashMap<String, TraitField>();
    private transient WorkingMemory workingMemory;

    private TypeCache typeCache = new TypeCache();

    private long modificationMask = Long.MIN_VALUE;

    public void init( WorkingMemory wm ) {
        this.workingMemory = wm;
        if ( getTypeCache().needsInit() ) {
            getTypeCache().init( wm );
        }
    }

    public TraitFieldTMSImpl() {
    }

    public void registerField( Class domainKlass, String name ) {
        registerField( domainKlass, name, Object.class, null, null );
    }

    public void registerField( Class domainKlass, String name, Class rangeKlass, Object value, String initial ) {
        short pos = (short) ClassUtils.getSettableProperties( domainKlass ).indexOf( name );
        TraitField fld = new TraitField( getKlass( rangeKlass ), value, initial != null ? MVEL.eval( initial, rangeKlass ) : null, pos );
        fieldTMS.put( name, fld );
    }

    public Object set( String name, Object value, Class klass ) {
        return fieldTMS.get( name ).set( value, getKlass( klass ), workingMemory );
    }

    public Object get( String name, Class klass ) {
        return fieldTMS.get( name ).get( getKlass( klass ) );
    }

    public boolean isManagingField( String name ) {
        return fieldTMS.containsKey( name );
    }

    public Object donField( String name, TraitType trait, String defaultValue, Class klass, boolean logical ) {
        TraitField fld = fieldTMS.get( name );
        modificationMask |= 1 << fld.getPosition();
        return fld.don( trait, defaultValue != null ? MVEL.eval( defaultValue, klass ) : null, getKlass( klass ), logical, workingMemory );
    }

    public Object shedField( String name, TraitType trait, Class rangeKlass, Class asKlass ) {
        TraitField fld = fieldTMS.get( name );
        modificationMask |= 1 << fld.getPosition();
        return fld.shed( trait, getKlass( rangeKlass ), getKlass( asKlass ), workingMemory );
    }

    private TypeWrapper getKlass( Class klass ) {
        TypeWrapper wrapper = getTypeCache().get( klass.getName() );
        if ( wrapper == null ) {
            wrapper = new TypeWrapper( klass );
            getTypeCache().put( wrapper.getName(), wrapper );
        }
        return wrapper;
    }

    public boolean needsInit() {
        return workingMemory == null;
    }

    public long getModificationMask() {
        return modificationMask;
    }

    public void resetModificationMask() {
        modificationMask = Long.MIN_VALUE;
    }

    public TraitField getRegisteredTraitField( String name ) {
        return fieldTMS.get( name );
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( fieldTMS.size() );
        List<String> keys = new ArrayList<String>( fieldTMS.keySet() );
        Collections.sort( keys );
        for ( String k : keys ) {
            out.writeObject( k );
            out.writeObject( fieldTMS.get( k ) );
        }

        out.writeObject( typeCache );

        out.writeLong( modificationMask );
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        fieldTMS = new HashMap<String, TraitField>();
        int n = in.readInt();
        for ( int j = 0; j < n; j++ ) {
            String k = (String) in.readObject();
            TraitField tf = (TraitField) in.readObject();
            fieldTMS.put( k, tf );
        }

        typeCache = (TypeCache) in.readObject();
        modificationMask = in.readLong();
    }

    public TypeCache getTypeCache() {
        if ( typeCache == null ) {
            typeCache = new TypeCache();
        }
        return typeCache;
    }



}
