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

package org.drools.compiler.factmodel.traits;

import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.TraitableBean;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ImpCoreWrapper extends Imp implements CoreWrapper<Imp>, TraitableBean<Imp,CoreWrapper<Imp>> {

    private Imp core;
    private Map<String,Object> __$$dynamic_properties_map$$;
    private Map<String,Thing<Imp>> __$$dynamic_traits_map$$;

    public Map<String, Object> _getDynamicProperties() {
        if ( __$$dynamic_properties_map$$ == null ) {
             __$$dynamic_properties_map$$ = new TraitTypeMap( new HashMap<String, Thing>() );
        }
        return __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties(Map<String, Object> map) {
        __$$dynamic_properties_map$$ = new TraitTypeMap( map );
    }


    public Map<String,Thing<Imp>> _getTraitMap() {
        if ( __$$dynamic_traits_map$$ == null ) {
            __$$dynamic_traits_map$$ = new TraitTypeMap( new HashMap<String, Thing<Imp>>() );
        }
        return __$$dynamic_traits_map$$;
    }

    public void addTrait(String type, Thing<Imp> proxy) throws LogicalTypeInconsistencyException {
        _getTraitMap().put( type, proxy );
    }

    public BitSet getCurrentTypeCode() {
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getCurrentTypeCode();
    }




    public void _setTraitMap(Map map) {
        this.__$$dynamic_traits_map$$ = map;
    }

    public Thing<Imp> getTrait(String type) {
        return _getTraitMap().get( type );
    }

    public boolean hasTrait(String type) {
        return _getTraitMap().containsKey( type );
    }

    public boolean hasTraits() {
        return __$$dynamic_traits_map$$ != null && ! __$$dynamic_traits_map$$.isEmpty();
    }

    public Collection<Thing<Imp>> removeTrait(String type) {
        return ((TraitTypeMap)_getTraitMap()).removeCascade( type );
    }

    public Collection<Thing<Imp>> removeTrait( BitSet typeCode ) {
        return ((TraitTypeMap)_getTraitMap()).removeCascade( typeCode );
    }

    public Collection<String> getTraits() {
        return _getTraitMap().keySet();
    }

    public Collection<Thing> getMostSpecificTraits() {
        return ((TraitTypeMap) _getTraitMap()).getMostSpecificTraits();
    }

    public void _setBottomTypeCode(BitSet code) {
        ((TraitTypeMap) __$$dynamic_traits_map$$).setBottomCode( code );
    }

    public TraitFieldTMS _getFieldTMS() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void _setFieldTMS( TraitFieldTMS tms ) {}

    public BitSet getBottomTypeCode() {
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getBottomCode();
    }

//    public Map getTraits() {
//        return __$$dynamic_traits_set$$;
//    }





    public void init(Imp core) {
        this.core = core;
    }


    public Map<String, Object> getFields() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Imp getCore() {
        return core;
    }



    public String getSchool() {
        return core.getSchool();
    }

    public void setSchool( String school ) {
        core.setSchool( school );
    }

    public String getName() {
        return core.getName();
    }

    public void setName( String name ) {
        core.setSchool( name );
    }





    public double testMethod( String arg1, int arg2, Object arg3, double arg4 ) {
        return core.testMethod(arg1,arg2, arg3, arg4);
    }


    public boolean equals( Object other ) {
        return core.equals( other );
    }

    public String toString() {
        return core.toString();
    }

    public int hashCode() {
        return core.hashCode();
    }


}
