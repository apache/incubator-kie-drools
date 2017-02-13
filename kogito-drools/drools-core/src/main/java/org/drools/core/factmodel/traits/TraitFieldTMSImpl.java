/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;

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

import org.drools.core.WorkingMemory;
import org.drools.core.util.ClassUtils;
import org.drools.core.util.MVELSafeHelper;
import org.drools.core.util.bitmask.BitMask;

import static org.drools.core.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;
import static org.drools.core.reteoo.PropertySpecificUtil.setPropertyOnMask;

public class TraitFieldTMSImpl implements TraitFieldTMS, Externalizable {

    private Map<String, TraitField> fieldTMS = new LinkedHashMap<String, TraitField>();
    private transient WorkingMemory workingMemory;

    private TypeCache typeCache = new TypeCache();

    private BitMask modificationMask = onlyTraitBitSetMask();

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
        short pos = (short) ClassUtils.getAccessibleProperties( domainKlass ).indexOf( name );
        TraitField fld = new TraitField( getKlass( rangeKlass ), value, initial != null ? MVELSafeHelper.getEvaluator().eval( initial, rangeKlass ) : null, pos );
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
        modificationMask = setPropertyOnMask(modificationMask, fld.getPosition());
        return fld.don( trait, defaultValue != null ? MVELSafeHelper.getEvaluator().eval( defaultValue, klass ) : null, getKlass( klass ), logical, workingMemory );
    }

    public Object shedField( String name, TraitType trait, Class rangeKlass, Class asKlass ) {
        TraitField fld = fieldTMS.get( name );
        modificationMask = setPropertyOnMask(modificationMask, fld.getPosition());
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

    public BitMask getModificationMask() {
        return modificationMask;
    }

    public void resetModificationMask() {
        modificationMask = onlyTraitBitSetMask();
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

        out.writeObject(modificationMask);
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
        modificationMask = (BitMask) in.readObject();
    }

    public TypeCache getTypeCache() {
        if ( typeCache == null ) {
            typeCache = new TypeCache();
        }
        return typeCache;
    }



}
