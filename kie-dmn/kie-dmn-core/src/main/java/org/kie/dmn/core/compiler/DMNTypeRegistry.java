package org.kie.dmn.core.compiler;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.feel.model.v1_1.DMNModelInstrumentedBase;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DMNTypeRegistry {

    private Map<String, Map<String, DMNType>> types = new HashMap<>(  );

    public DMNTypeRegistry() {
        String feelNamespace = DMNModelInstrumentedBase.URI_FEEL;
        Map<String, DMNType> feelTypes = new HashMap<>(  );
        types.put( feelNamespace, feelTypes );

        DMNType unknown = new SimpleTypeImpl( feelNamespace, BuiltInType.UNKNOWN.getName(), null, true, null, null, BuiltInType.UNKNOWN );
        feelTypes.put( unknown.getName(), unknown );

        for( BuiltInType type : BuiltInType.values() ) {
            for( String name : type.getNames() ) {
                DMNType feelPrimitiveType;
                if( type == BuiltInType.UNKNOWN ) {
                    // already added, skip it
                    continue;
                } else if( type == BuiltInType.LIST ) {
                    feelPrimitiveType = new SimpleTypeImpl( feelNamespace, name, null, true, null, unknown, type );
                } else if( type == BuiltInType.CONTEXT ) {
                    feelPrimitiveType = new CompositeTypeImpl( feelNamespace, name, null, false, Collections.emptyMap(), null, type );
                } else {
                    feelPrimitiveType = new SimpleTypeImpl( feelNamespace, name, null, false, null, null, type );
                }
                feelTypes.put( name, feelPrimitiveType );
            }
        }
    }

    public void registerType( DMNType type ) {
        if( type.getNamespace() == null && type.getName() == null ) {
            throw new IllegalArgumentException( "Unknown namespace or name. Unable to register type "+type );
        }

        Map<String, DMNType> typesMap = this.types.get( type.getNamespace() );
        if( typesMap == null ) {
            typesMap = new HashMap<>(  );
            this.types.put( type.getNamespace(), typesMap );
        }
        if( typesMap.containsKey( type.getName() ) ) {
            throw new IllegalArgumentException( "Type name already registered. Existing type: "+types.get( type.getName() )+" - new type: "+type );
        }
        typesMap.put( type.getName(), type );
    }

    public DMNType resolveType( String namespace, String name ) {
        Map<String, DMNType> typeMap = types.get( namespace );
        if( typeMap != null ) {
            return typeMap.get( name );
        }
        return null;
    }


}
