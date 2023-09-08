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
package org.drools.traits.core.factmodel;

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
import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.base.factmodel.traits.TraitType;
import org.drools.base.util.PropertyReactivityUtil;
import org.drools.util.bitmask.BitMask;
import org.drools.mvel.MVELSafeHelper;

import static org.drools.base.reteoo.PropertySpecificUtil.onlyTraitBitSetMask;
import static org.drools.base.reteoo.PropertySpecificUtil.setPropertyOnMask;

public class TraitFieldTMSImpl implements TraitFieldTMS, Externalizable {

    private Map<String, TraitFieldImpl> fieldTMS = new LinkedHashMap<>();
    private transient WorkingMemory workingMemory;

    private TypeCache typeCache = new TypeCache();

    private BitMask modificationMask = onlyTraitBitSetMask();

    public void init( Object wm ) {
        this.workingMemory = (WorkingMemory) wm;
        if ( getTypeCache().needsInit() ) {
            getTypeCache().init( workingMemory );
        }
    }

    public TraitFieldTMSImpl() {
    }

    public void registerField( Class domainKlass, String name ) {
        registerField( domainKlass, name, Object.class, null, null );
    }

    public void registerField( Class domainKlass, String name, Class rangeKlass, Object value, String initial ) {
        short pos = (short) PropertyReactivityUtil.getAccessibleProperties( domainKlass ).indexOf( name );
        TraitFieldImpl fld = new TraitFieldImpl(getKlass(rangeKlass ), value, initial != null ? MVELSafeHelper.getEvaluator().eval(initial, rangeKlass ) : null, pos );
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
        TraitFieldImpl fld = fieldTMS.get(name );
        modificationMask = setPropertyOnMask(modificationMask, fld.getPosition());
        return fld.don( trait, defaultValue != null ? MVELSafeHelper.getEvaluator().eval( defaultValue, klass ) : null, getKlass( klass ), logical, workingMemory );
    }

    public Object shedField(String name, TraitType trait, Class rangeKlass, Class asKlass ) {
        TraitFieldImpl fld = fieldTMS.get(name );
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

    public TraitFieldImpl getRegisteredTraitField(String name ) {
        return fieldTMS.get( name );
    }

    public void writeExternal( ObjectOutput out ) throws IOException {
        out.writeInt( fieldTMS.size() );
        List<String> keys = new ArrayList<>( fieldTMS.keySet() );
        Collections.sort( keys );
        for ( String k : keys ) {
            out.writeObject( k );
            out.writeObject( fieldTMS.get( k ) );
        }

        out.writeObject( typeCache );

        out.writeObject(modificationMask);
    }

    public void readExternal( ObjectInput in ) throws IOException, ClassNotFoundException {
        fieldTMS = new HashMap<>();
        int n = in.readInt();
        for ( int j = 0; j < n; j++ ) {
            String k = (String) in.readObject();
            TraitFieldImpl tf = (TraitFieldImpl) in.readObject();
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

	@Override
	public String toString() {
		return "TraitFieldTMSImpl{}";
	}
}
