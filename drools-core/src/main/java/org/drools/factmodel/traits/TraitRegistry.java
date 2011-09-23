/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.factmodel.traits;

import org.drools.definition.type.FactField;
import org.drools.factmodel.ClassDefinition;
import org.drools.factmodel.FieldDefinition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class TraitRegistry {

    private static TraitRegistry instance;

    public static TraitRegistry getInstance() {
        if ( instance == null ) {
            instance = new TraitRegistry();
        }
        return instance;
    }

    public static void reset() {
        instance = null;
    }

    private Map<String, ClassDefinition> traits;
    private Map<String, ClassDefinition> traitables;

    private Map<String, Long> masks;


    private TraitRegistry() {
        ClassDefinition individualDef = new ClassDefinition();
            individualDef.setClassName( Entity.class.getName() );
            individualDef.setDefinedClass(Entity.class);
            individualDef.setInterfaces( new String[] { Serializable.class.getName(), TraitableBean.class.getName() } );
            individualDef.setTraitable( true );
        addTraitable( individualDef );

    }


    public Map<String, ClassDefinition> getTraits() {
        return traits;
    }

    protected ClassDefinition getTrait( String key ) {
        if ( key.endsWith(  TraitFactory.SUFFIX ) ) {
            key = key.replace(  TraitFactory.SUFFIX , "" );
        }
        return traits != null ? traits.get( key ) : null;
    }

    public Map<String, ClassDefinition> getTraitables() {
        return traitables;
    }

    protected ClassDefinition getTraitable( String key ) {
        return traitables != null ? traitables.get( key ) : null;
    }


    public void addTrait( ClassDefinition trait ) {
        addTrait( trait.getClassName(), trait );
    }

    public void addTrait( String className, ClassDefinition trait ) {
        if ( traits == null ) {
            traits = new HashMap<String, ClassDefinition>();
        }
        this.traits.put( className, trait );
    }

    public void addTraitable( ClassDefinition traitable ) {
        if ( traitables == null ) {
            traitables = new HashMap<String, ClassDefinition>();
        }
        this.traitables.put( traitable.getClassName(), traitable );
    }


    public long getFieldMask( String trait, String traitable ) {
        if ( masks == null ) {
            masks = new HashMap<String, Long>();
        }
        String key = trait + traitable;
        Long mask = masks.get( key );

        if ( mask == null ) {
            mask = bind( trait, traitable );
            masks.put( key, mask );
        }

        return mask;
    }

    private Long bind( String trait, String traitable ) throws UnsupportedOperationException {
        ClassDefinition traitDef = getTrait( trait );
            if ( traitDef == null ) {
                throw new UnsupportedOperationException( " Unable to apply trait " + trait + " to class " + traitable + " : not a trait " );
            }
        ClassDefinition traitableDef = getTraitable( traitable );
            if ( traitableDef == null ) {
                throw new UnsupportedOperationException( " Unable to apply trait " + trait + " to class " + traitable + " : not a traitable " );
            }

        int j = 0;
        long bitmask = 0;
        for ( FactField field : traitDef.getFields() ) {
            FieldDefinition concreteField = traitableDef.getField( field.getName() );
                if ( concreteField != null
                     && concreteField.getType().isAssignableFrom( field.getType() ) ) {
                bitmask |= 1 << j;
            }
            j++;
        }

        return bitmask;
    }




}
