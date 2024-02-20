/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kie.dmn.core.compiler;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.xml.namespace.QName;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.BaseDMNTypeImpl;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.Scope;
import org.kie.dmn.feel.lang.Type;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.lang.types.FEELTypeRegistry;
import org.kie.dmn.feel.lang.types.ScopeImpl;
import org.kie.dmn.feel.lang.types.TypeSymbol;
import org.kie.dmn.feel.lang.types.WrappingScopeImpl;

public abstract class DMNTypeRegistryAbstract implements DMNTypeRegistry, FEELTypeRegistry {

    protected Map<String, Map<String, DMNType>> types = new HashMap<>();
    protected Map<String, QName> aliases;
    protected ScopeImpl feelTypesScope = new ScopeImpl(); // no parent scope, intentional.
    protected Map<String, ScopeImpl> feelTypesScopeChildLU = new HashMap<>();


    public DMNTypeRegistryAbstract(Map<String, QName> aliases) {
        this.aliases = aliases;
        String feelNamespace = feelNS();
        Map<String, DMNType> feelTypes = new HashMap<>(  );
        types.put( feelNamespace, feelTypes );

        for (String name : BuiltInType.UNKNOWN.getNames()) {
            feelTypes.put(name, unknown());
            feelTypesScope.define(new TypeSymbol(name, BuiltInType.UNKNOWN));
        }

        for( BuiltInType type : BuiltInType.values() ) {
            for( String name : type.getNames() ) {
                DMNType feelPrimitiveType;
                if( type == BuiltInType.UNKNOWN ) {
                    // already added, skip it
                    continue;
                } else if( type == BuiltInType.LIST ) {
                    feelPrimitiveType = new SimpleTypeImpl(feelNamespace, name, null, false, null, null, unknown(), type);
                } else if( type == BuiltInType.CONTEXT ) {
                    feelPrimitiveType = new CompositeTypeImpl( feelNamespace, name, null, false, Collections.emptyMap(), null, type );
                } else {
                    feelPrimitiveType = new SimpleTypeImpl( feelNamespace, name, null, false, null, null, null, type );
                }
                feelTypes.put( name, feelPrimitiveType );
                feelTypesScope.define(new TypeSymbol(name, type));
            }
        }
    }

    @Override
    public Scope getItemDefScope(Scope parent) {
        return new WrappingScopeImpl(feelTypesScope, parent);
    }

    @Override
    public Type resolveFEELType(List<String> qns) {
        if (qns.size() == 1) {
            return feelTypesScope.resolve(qns.get(0)).getType();
        } else if (qns.size() == 2 && feelTypesScopeChildLU.containsKey(qns.get(0))) {
            return feelTypesScopeChildLU.get(qns.get(0)).resolve(qns.get(1)).getType();
        } else {
            throw new IllegalStateException("Inconsistent state when resolving for qns: " + qns.toString());
        }
    }

    @Override
    public Map<String, Map<String, DMNType>> getTypes() {
        return types;
    }

    protected void registerAsFEELType(DMNType dmnType) {
        Optional<String> optAliasKey = keyfromNS(dmnType.getNamespace());
        Type feelType = ((BaseDMNTypeImpl) dmnType).getFeelType();
        if (optAliasKey.isEmpty()) {
            feelTypesScope.define(new TypeSymbol(dmnType.getName(), feelType));
        } else {
            String aliasKey = optAliasKey.get();
            feelTypesScopeChildLU.computeIfAbsent(aliasKey, k -> {
                ScopeImpl importScope = new ScopeImpl(k, feelTypesScope);
                feelTypesScope.define(new TypeSymbol(k, null));
                return importScope;
            }).define(new TypeSymbol(dmnType.getName(), feelType));
        }
    }

    private Optional<String> keyfromNS(String ns) {
        return aliases == null ? Optional.empty() : aliases.entrySet().stream().filter(kv -> kv.getValue().getNamespaceURI().equals(ns)).map(kv -> kv.getKey()).findFirst();
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
        registerAsFEELType(type);
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
