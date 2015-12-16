/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.factmodel.traits;

import org.kie.api.definition.type.PropertyReactive;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Traitable
@PropertyReactive
public class Entity implements TraitableBean<Entity,Entity>, Serializable {


    private String id;

    private TraitFieldTMS __$$field_Tms$$ = new TraitFieldTMSImpl();
    private Map<String,Object> __$$dynamic_properties_map$$;
    private Map<String,Thing<Entity>> __$$dynamic_traits_map$$;

    public Entity() {
        id = UUID.randomUUID().toString();
    }

    public Entity(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> _getDynamicProperties() {
        return  __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties(Map map) {
        __$$dynamic_properties_map$$ = map;
    }


    public TraitFieldTMS _getFieldTMS() {
        if ( __$$field_Tms$$ == null ) {
            __$$field_Tms$$ = new TraitFieldTMSImpl();
        }
        return __$$field_Tms$$;
    }

    public void _setFieldTMS( TraitFieldTMS __$$field_Tms$$ ) {
        this.__$$field_Tms$$ = __$$field_Tms$$;
    }

    public void _setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }


    public Map<String, Thing<Entity>> _getTraitMap() {
        return __$$dynamic_traits_map$$;
    }

    public void addTrait(String type, Thing proxy) throws LogicalTypeInconsistencyException {
       ((TraitTypeMap) _getTraitMap()).putSafe(type, proxy);
    }

    public Thing getTrait(String type) {
        return _getTraitMap().get( type );
    }

    public boolean hasTrait(String type) {
        return isTraitMapInitialized() && _getTraitMap().containsKey(type);
    }

    public boolean hasTraits() {
        return __$$dynamic_traits_map$$ != null && ! __$$dynamic_traits_map$$.isEmpty();
    }

    public Collection<Thing<Entity>> removeTrait( String type ) {
        if ( isTraitMapInitialized() ) {
            return ((TraitTypeMap)_getTraitMap()).removeCascade(type);
        } else {
            return null;
        }        
    }

    public Collection<Thing<Entity>> removeTrait( BitSet typeCode ) {
        if ( isTraitMapInitialized() ) {
            return ((TraitTypeMap)_getTraitMap()).removeCascade( typeCode );
        } else {
            return null;
        }
    }

    public Collection<String> getTraits() {
        if ( isTraitMapInitialized() ) {
            return _getTraitMap().keySet();
        } else {
            return Collections.emptySet();
        }
    }

    public Collection<Thing> getMostSpecificTraits() {
        if ( __$$dynamic_traits_map$$ == null ) {
            return Collections.EMPTY_LIST;
        }
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getMostSpecificTraits();
    }

    public BitSet getCurrentTypeCode() {
        if ( __$$dynamic_traits_map$$ == null ) {
            return null;
        }
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getCurrentTypeCode();
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity that = (Entity) o;

        return !(id != null ? !id.equals(that.id) : that.id != null);

    }

    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    public boolean isTraitMapInitialized() {
        return __$$dynamic_traits_map$$ != null;
    }


    public void _setBottomTypeCode( BitSet bottomTypeCode ) {
        ((TraitTypeMap) __$$dynamic_traits_map$$).setBottomCode( bottomTypeCode );
    }

    @Override
    public String toString() {
        return "Entity{" +
               "id='" + id + '\'' +
               '}';
    }
}

