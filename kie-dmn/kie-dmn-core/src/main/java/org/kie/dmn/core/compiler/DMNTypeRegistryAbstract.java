/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.core.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;

public abstract class DMNTypeRegistryAbstract implements DMNTypeRegistry {

    protected Map<String, Map<String, DMNType>> types = new HashMap<>();

    protected abstract String feelNS();

    public DMNTypeRegistryAbstract() {
        String feelNamespace = feelNS();
        Map<String, DMNType> feelTypes = new HashMap<>(  );
        types.put( feelNamespace, feelTypes );

        for (String name : BuiltInType.UNKNOWN.getNames()) {
            feelTypes.put(name, unknown());
        }

        for( BuiltInType type : BuiltInType.values() ) {
            for( String name : type.getNames() ) {
                DMNType feelPrimitiveType;
                if( type == BuiltInType.UNKNOWN ) {
                    // already added, skip it
                    continue;
                } else if( type == BuiltInType.LIST ) {
                    feelPrimitiveType = new SimpleTypeImpl(feelNamespace, name, null, false, null, unknown(), type);
                } else if( type == BuiltInType.CONTEXT ) {
                    feelPrimitiveType = new CompositeTypeImpl( feelNamespace, name, null, false, Collections.emptyMap(), null, type );
                } else {
                    feelPrimitiveType = new SimpleTypeImpl( feelNamespace, name, null, false, null, null, type );
                }
                feelTypes.put( name, feelPrimitiveType );
            }
        }
    }

    @Override
    public DMNType registerType( DMNType type ) {
        if( type.getNamespace() == null && type.getName() == null ) {
            throw new IllegalArgumentException( "Unknown namespace or name. Unable to register type "+type );
        }

        Map<String, DMNType> typesMap = this.types.get( type.getNamespace() );
        if( typesMap == null ) {
            typesMap = new HashMap<>(  );
            this.types.put( type.getNamespace(), typesMap );
        }
        if( typesMap.containsKey( type.getName() ) ) {
            return typesMap.get( type.getName() );
        }
        typesMap.put( type.getName(), type );
        return type;
    }

    @Override
    public DMNType resolveType( String namespace, String name ) {
        Map<String, DMNType> typeMap = types.get( namespace );
        if( typeMap != null ) {
            return typeMap.get( name );
        }
        return null;
    }


}
