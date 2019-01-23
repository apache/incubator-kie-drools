package org.kie.dmn.core.compiler;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.dmn.api.core.DMNType;
import org.kie.dmn.core.impl.CompositeTypeImpl;
import org.kie.dmn.core.impl.SimpleTypeImpl;
import org.kie.dmn.feel.lang.types.BuiltInType;
import org.kie.dmn.model.v1_2.KieDMNModelInstrumentedBase;

public class DMNTypeRegistryV12 implements DMNTypeRegistry {

    private Map<String, Map<String, DMNType>> types = new HashMap<>(  );

    private static final DMNType UNKNOWN = new SimpleTypeImpl(KieDMNModelInstrumentedBase.URI_FEEL,
                                                              BuiltInType.UNKNOWN.getName(),
                                                              null, true, null, null,
                                                              BuiltInType.UNKNOWN );

    @Override
    public DMNType unknown() {
        return UNKNOWN;
    }

    /** 
     * DMN v1.2 spec, chapter 7.3.2 ItemDefinition metamodel
     * FEEL built-in data types: number, string, boolean, days and time duration, years and months duration, time, and date and time. Was missing from spec document: date, Any, list, function, context.
     */
    public static final List<BuiltInType> ITEMDEF_TYPEREF_FEEL_BUILTIN = Collections.unmodifiableList(Arrays.asList(BuiltInType.NUMBER,
                                                                                                                    BuiltInType.STRING,
                                                                                                                    BuiltInType.BOOLEAN,
                                                                                                                    BuiltInType.DURATION,
                                                                                                                    BuiltInType.DATE,
                                                                                                                    BuiltInType.TIME,
                                                                                                                    BuiltInType.DATE_TIME,
                                                                                                                    BuiltInType.UNKNOWN,
                                                                                                                    BuiltInType.LIST,
                                                                                                                    BuiltInType.FUNCTION,
                                                                                                                    BuiltInType.CONTEXT));

    public DMNTypeRegistryV12() {
        String feelNamespace = KieDMNModelInstrumentedBase.URI_FEEL;
        Map<String, DMNType> feelTypes = new HashMap<>(  );
        types.put( feelNamespace, feelTypes );

        for (String name : BuiltInType.UNKNOWN.getNames()) {
            feelTypes.put(name, UNKNOWN);
        }

        for( BuiltInType type : BuiltInType.values() ) {
            for( String name : type.getNames() ) {
                DMNType feelPrimitiveType;
                if( type == BuiltInType.UNKNOWN ) {
                    // already added, skip it
                    continue;
                } else if( type == BuiltInType.LIST ) {
                    feelPrimitiveType = new SimpleTypeImpl( feelNamespace, name, null, false, null, UNKNOWN, type );
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
