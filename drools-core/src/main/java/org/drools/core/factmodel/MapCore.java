package org.drools.core.factmodel;

import org.drools.core.factmodel.traits.CoreWrapper;
import org.drools.core.factmodel.traits.LogicalTypeInconsistencyException;
import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitTypeMap;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.factmodel.traits.TraitableBean;
import org.drools.core.rule.TypeDeclaration;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Traitable
public class MapCore<K> implements TraitableBean<Map,CoreWrapper<Map>>, Serializable {

    private String id;

    private Map<String,Object> __$$dynamic_properties_map$$;
    private Map<String,Thing<Map>> __$$dynamic_traits_map$$;

    public MapCore() {

    }

    public MapCore(Map map) {
        id = UUID.randomUUID().toString();
        __$$dynamic_properties_map$$ = map;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> _getDynamicProperties() {
        return __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties( Map map ) {
        // can't be changed
    }


    public void _setTraitMap(Map map) {
        __$$dynamic_traits_map$$ = map;
    }


    public Map<String, Thing<Map>> _getTraitMap() {
        return __$$dynamic_traits_map$$;
    }

    public void addTrait(String type, Thing proxy) throws LogicalTypeInconsistencyException {
        ((TraitTypeMap) _getTraitMap()).putSafe(type, proxy);
    }

    public Thing getTrait(String type) {
        return _getTraitMap().get( type );
    }

    public boolean hasTrait(String type) {
        return isTraitMapInitialized() && _getTraitMap().containsKey(type);
    }

    public Collection<Thing<Map>> removeTrait( String type ) {
        if ( isTraitMapInitialized() ) {
            return ((TraitTypeMap)_getTraitMap()).removeCascade(type);
        } else {
            return null;
        }
    }

    public Collection<Thing<Map>> removeTrait( BitSet typeCode ) {
        if ( isTraitMapInitialized() ) {
            return ((TraitTypeMap)_getTraitMap()).removeCascade( typeCode );
        } else {
            return null;
        }
    }

    public Collection<String> getTraits() {
        if ( isTraitMapInitialized() ) {
            return _getTraitMap().keySet();
        } else {
            return Collections.emptySet();
        }
    }

    public Collection<Thing> getMostSpecificTraits() {
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getMostSpecificTraits();
    }

    public BitSet getCurrentTypeCode() {
        return ((TraitTypeMap) __$$dynamic_traits_map$$).getCurrentTypeCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCore mapCore = (MapCore) o;

        if (!__$$dynamic_properties_map$$.equals(mapCore.__$$dynamic_properties_map$$)) return false;
        if (!__$$dynamic_traits_map$$.equals(mapCore.__$$dynamic_traits_map$$)) return false;

        return true;
    }

    public int hashCode() {
        return __$$dynamic_properties_map$$.hashCode() ^ __$$dynamic_traits_map$$.hashCode();
    }

    public boolean isTraitMapInitialized() {
        return __$$dynamic_traits_map$$ != null;
    }


    public void _setBottomTypeCode( BitSet bottomTypeCode ) {
        ((TraitTypeMap) __$$dynamic_traits_map$$).setBottomCode( bottomTypeCode );
    }

    public void init( MapCore core ) {
    }

    public Map getCore() {
        return __$$dynamic_properties_map$$;
    }

    public static TypeDeclaration getTypeDeclaration() {

        String[] mcInterfaces = new String[ MapCore.class.getInterfaces().length ];
        int j = 0;
        for ( Class intf : MapCore.class.getInterfaces() ) {
            mcInterfaces[ j++ ] = intf.getName();
        }
        ClassDefinition mcClassDef = new ClassDefinition( MapCore.class.getName(), MapCore.class.getSuperclass().getName(), mcInterfaces );
        mcClassDef.setTraitable( true );
        mcClassDef.setDefinedClass( MapCore.class );
        mcClassDef.setAbstrakt( false );

        TypeDeclaration mapCoreType = new TypeDeclaration( MapCore.class.getName() );
        mapCoreType.setKind( TypeDeclaration.Kind.CLASS );
        mapCoreType.setTypeClass( MapCore.class );
        mapCoreType.setTypeClassDef( mcClassDef );

        return mapCoreType;
    }
}