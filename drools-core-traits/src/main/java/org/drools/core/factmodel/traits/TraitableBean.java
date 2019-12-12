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

import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

public interface TraitableBean<K, X extends TraitableBean> {

    String MAP_FIELD_NAME = "__$$dynamic_properties_map$$";
    String TRAITSET_FIELD_NAME = "__$$dynamic_traits_map$$";
    String FIELDTMS_FIELD_NAME = "__$$field_Tms$$";

    Map<String,Object> _getDynamicProperties();

    void _setDynamicProperties( Map<String,Object> map );

    Map<String,Thing<K>> _getTraitMap();

    void _setTraitMap( Map<String,Thing<K>> map );


    TraitFieldTMS _getFieldTMS();

    void _setFieldTMS( TraitFieldTMS traitFieldTMS );


	default void addTrait(String type, Thing proxy) throws LogicalTypeInconsistencyException {
		((TraitTypeMap) _getTraitMap()).putSafe(type, proxy);
	}

	default Thing<K> getTrait(String type) {
		return _getTraitMap().get( type );
	}

	default boolean hasTrait(String type) {
		return isTraitMapInitialized() && _getTraitMap().containsKey(type);
	}

	default boolean hasTraits() {
		return _getTraitMap() != null && ! _getTraitMap().isEmpty();
	}

	default Collection<Thing<K>> removeTrait( String type ) {
		if ( isTraitMapInitialized() ) {
			return ((TraitTypeMap)_getTraitMap()).removeCascade(type);
		} else {
			return null;
		}
	}

	default Collection<Thing<K>> removeTrait( BitSet typeCode ) {
		if ( isTraitMapInitialized() ) {
			return ((TraitTypeMap)_getTraitMap()).removeCascade( typeCode );
		} else {
			return null;
		}
	}

	default Collection<String> getTraits() {
		if ( isTraitMapInitialized() ) {
			return _getTraitMap().keySet();
		} else {
			return Collections.emptySet();
		}
	}

	default Collection<Thing<K>> getMostSpecificTraits() {
		if ( _getTraitMap() == null ) {
			return Collections.emptyList();
		}
		return ((TraitTypeMap) _getTraitMap()).getMostSpecificTraits();
	}

	default BitSet getCurrentTypeCode() {
		if ( _getTraitMap() == null ) {
			return null;
		}
		return ((TraitTypeMap) _getTraitMap()).getCurrentTypeCode();
	}

	default boolean isTraitMapInitialized() {
		return _getTraitMap() != null;
	}


	default void _setBottomTypeCode( BitSet bottomTypeCode ) {
		((TraitTypeMap) _getTraitMap()).setBottomCode( bottomTypeCode );
	}


}
