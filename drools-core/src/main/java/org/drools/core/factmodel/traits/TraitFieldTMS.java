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

import org.drools.core.WorkingMemory;
import org.drools.core.util.bitmask.BitMask;

import java.io.Externalizable;

public interface TraitFieldTMS extends Externalizable {

    // Most of the code generation for traits is still in drools-core DefaultBeanClassBuilder so this module needs to know the name of the impl class for traits
    String TYPE_NAME = "org/drools/traits/core/factmodel/TraitFieldTMSImpl";

    void init( WorkingMemory wm );

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
