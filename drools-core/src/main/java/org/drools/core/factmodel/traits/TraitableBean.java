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

package org.drools.core.factmodel.traits;

import java.util.BitSet;
import java.util.Collection;
import java.util.Map;

public interface TraitableBean<K, X extends TraitableBean> {

    public static final String MAP_FIELD_NAME = "__$$dynamic_properties_map$$";
    public String TRAITSET_FIELD_NAME = "__$$dynamic_traits_map$$";
    public static String FIELDTMS_FIELD_NAME = "__$$field_Tms$$";

    public Map<String,Object> _getDynamicProperties();

    public void _setDynamicProperties( Map<String,Object> map );

    public Map<String,Thing<K>> _getTraitMap();

    public void _setTraitMap( Map<String,Thing<K>> map );


    public void addTrait(String type, Thing<K> proxy) throws LogicalTypeInconsistencyException;

    public Thing<K> getTrait( String type );

    public boolean hasTrait( String type );

    public boolean hasTraits();

    public Collection<Thing<K>> removeTrait( String type );

    public Collection<Thing<K>> removeTrait( BitSet typeCode );

    public Collection<String> getTraits();


    public Collection<Thing> getMostSpecificTraits();

    public BitSet getCurrentTypeCode();

    public void _setBottomTypeCode( BitSet code );


    public TraitFieldTMS _getFieldTMS();

    void _setFieldTMS( TraitFieldTMS traitFieldTMS );
}
