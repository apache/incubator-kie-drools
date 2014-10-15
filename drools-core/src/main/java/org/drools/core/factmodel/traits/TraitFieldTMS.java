package org.drools.core.factmodel.traits;

import org.drools.core.WorkingMemory;
import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;

public interface TraitFieldTMS extends Externalizable {

    public void init( WorkingMemory wm );

    public boolean needsInit();



    public void registerField( Class domainKlass, String name );

    public void registerField( Class domainKlass, String name, Class klass, Object value, String initial );

    public boolean isManagingField( String name );

    public TraitField getRegisteredTraitField( String name );



    public Object set( String name, Object value, Class klass );

    public Object get( String name, Class klass );


    public Object donField( String name, TraitType trait, String value, Class klass, boolean logical );

    public Object shedField( String name, TraitType trait, Class rangeKlass, Class asKlass );


    public BitMask getModificationMask();

    public void resetModificationMask();

}
