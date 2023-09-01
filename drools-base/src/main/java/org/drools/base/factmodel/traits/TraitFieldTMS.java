package org.drools.base.factmodel.traits;

import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;

public interface TraitFieldTMS extends Externalizable {

    // Most of the code generation for traits is still in drools-core DefaultBeanClassBuilder so this module needs to know the name of the impl class for traits
    String TYPE_NAME = "org/drools/traits/core/factmodel/TraitFieldTMSImpl";

    void init( Object wm );

    boolean needsInit();



    void registerField( Class domainKlass, String name );

    void registerField( Class domainKlass, String name, Class klass, Object value, String initial );

    boolean isManagingField( String name );

    TraitField getRegisteredTraitField( String name );



    Object set( String name, Object value, Class klass );

    Object get( String name, Class klass );


    Object donField( String name, TraitType trait, String value, Class klass, boolean logical );

    Object shedField( String name, TraitType trait, Class rangeKlass, Class asKlass );


    BitMask getModificationMask();

    void resetModificationMask();

}
