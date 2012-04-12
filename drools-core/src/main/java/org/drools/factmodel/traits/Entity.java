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

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Traitable
public class Entity implements TraitableBean, Serializable {


    private String id;

    private Map<String,Object> __$$dynamic_properties_map$$;
    private Map<String,Thing> __$$dynamic_traits_map$$;

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

    public Map<String, Object> getDynamicProperties() {
        if ( __$$dynamic_properties_map$$ == null ) {
            __$$dynamic_properties_map$$ = new HashMap<String,Object>(5);
        }
        return  __$$dynamic_properties_map$$;
    }

    public void setDynamicProperties(Map map) {
        __$$dynamic_properties_map$$ = map;
    }



    public void setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }


    public Map<String, Thing> getTraitMap() {
        if ( __$$dynamic_traits_map$$ == null ) {
            __$$dynamic_traits_map$$ = new HashMap<String, Thing>(5);
        }
        return __$$dynamic_traits_map$$;
    }

    public void addTrait(String type, Thing proxy) {
        getTraitMap().put(type, proxy);
    }

    public Thing getTrait(String type) {
        return getTraitMap().get( type );
    }

    public boolean hasTrait(String type) {
        return isTraitMapInitialized() && getTraitMap().containsKey(type);
    }

    public Thing removeTrait(String type) {
        if ( isTraitMapInitialized() ) {
            return getTraitMap().remove( type );    
        } else {
            return null;
        }        
    }

    public Collection<String> getTraits() {
        if ( isTraitMapInitialized() ) {
            return getTraitMap().keySet();
        } else {
            return Collections.emptySet();
        }
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
}

