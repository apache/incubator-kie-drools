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

package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitFieldTMSImpl;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Imp2 implements TraitableBean<Imp2,Imp2> {

    private String name;
    private String school;

    private int age;

    public int getAge() {
        return age;
    }

    public void setAge( int age ) {
        this.age = age;
    }

    private Map<String,Object> __$$dynamic_properties_map$$ = new HashMap<String,Object>();
    private Map<String,Thing<Imp2>> __$$dynamic_traits_map$$;

    public Collection foo2() {
        if ( __$$dynamic_traits_map$$ != null ) {
            return __$$dynamic_traits_map$$.keySet();
        } else {
            return Collections.emptySet();
        }
    }

    public Map<String,Object> _getDynamicProperties() {
        return __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties( Map<String,Object> map ) {
        __$$dynamic_properties_map$$ = map;
    }


    private Map<String, ? extends Thing> traits = new HashMap<String, Thing>();

    public void _setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }

    public boolean hasTraits() {
        return __$$dynamic_traits_map$$ != null && ! __$$dynamic_traits_map$$.isEmpty();
    }

    public Map<String, Thing<Imp2>> _getTraitMap() {
        if ( __$$dynamic_traits_map$$ == null ) {
            __$$dynamic_traits_map$$ = new HashMap<String, Thing<Imp2>>();
        }
        return __$$dynamic_traits_map$$;
    }

    public void addTrait(String type, Thing proxy) {
        _getTraitMap().put(type, proxy);
    }

    public Thing getTrait(String type) {
        return _getTraitMap().get( type );
    }

    public boolean hasTrait(String type) {
        return _getTraitMap().containsKey(type);
    }

    public BitSet getCurrentTypeCode() {
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getCurrentTypeCode();
    }

    public Collection<Thing<Imp2>> removeTrait(String type) {
        return ((TraitTypeMap)_getTraitMap()).removeCascade( type );
    }

    public Collection<Thing<Imp2>> removeTrait( BitSet typeCode ) {
        return ((TraitTypeMap)_getTraitMap()).removeCascade( typeCode );
    }

    public Collection<String> getTraits() {
        return _getTraitMap().keySet();
    }

    public void denyTrait(Class trait) throws LogicalTypeInconsistencyException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void allowTrait(Class trait) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public Collection<Thing> getMostSpecificTraits() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _setBottomTypeCode(BitSet code) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public TraitFieldTMS _getFieldTMS() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _setFieldTMS( TraitFieldTMS tms ) {}

    public BitSet _getBottomTypeCode() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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
        this.school = (String) tms.set( "school", school, String.class );
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

    public void foo() {
        Object f = __$$dynamic_properties_map$$.get( "goo" );
        System.out.println( f );

    }

    private TraitFieldTMS tms;

    public void init() {
        tms = new TraitFieldTMSImpl();
        tms.registerField( Imp2.class, "name", String.class, this.getName(), "foo" );
    }
}
