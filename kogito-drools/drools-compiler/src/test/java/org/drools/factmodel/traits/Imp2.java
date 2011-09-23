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

import java.util.*;

public class Imp2 {

    private String name;
    private String school;


    private Map<String,Object> __$$dynamic_properties_map$$ = new HashMap<String,Object>();
    private Map<String,Thing> __$$dynamic_traits_map$$;

    public Map<String,Object> getDynamicProperties() {
        return __$$dynamic_properties_map$$;
    }

    public void setDynamicProperties( Map<String,Object> map ) {
        __$$dynamic_properties_map$$ = map;
    }


    private Map<String, ? extends Thing> traits = new HashMap<String, Thing>();

    public void setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }


    public Map<String, Thing> getTraitMap() {
        if ( __$$dynamic_traits_map$$ == null ) {
            __$$dynamic_traits_map$$ = new HashMap<String, Thing>();
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
        return getTraitMap().containsKey(type);
    }

    public Thing removeTrait(String type) {
        return getTraitMap().remove( type );
    }

    public Collection<String> getTraits() {
        return getTraitMap().keySet();
    }









    private Set field;
    private Map field2;

    public Imp2() {
        field = new HashSet();
        field2 = new HashMap();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSchool() {
        return school;
    }

    public void setSchool(String school) {
        this.school = school;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Imp2 imp2 = (Imp2) o;

        if (name != null ? !name.equals(imp2.name) : imp2.name != null) return false;
        if (school != null ? !school.equals(imp2.school) : imp2.school != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (school != null ? school.hashCode() : 0);
        return result;
    }
}
