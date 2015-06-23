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
