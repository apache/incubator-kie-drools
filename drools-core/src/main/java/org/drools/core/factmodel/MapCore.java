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

package org.drools.core.factmodel;

import org.drools.core.factmodel.traits.Thing;
import org.drools.core.factmodel.traits.TraitFieldTMS;
import org.drools.core.factmodel.traits.TraitFieldTMSImpl;
import org.drools.core.factmodel.traits.Traitable;
import org.drools.core.rule.TypeDeclaration;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Traitable
public class MapCore<K> implements TraitableMap {

    private String id;

    private Map<String,Object> __$$dynamic_properties_map$$;

    public MapCore() {

    }

    public MapCore( Map map ) {
        id = UUID.randomUUID().toString();
        __$$dynamic_properties_map$$ = map;
    }

    public TraitFieldTMS _getFieldTMS() {
    	TraitFieldTMS tms = ( TraitFieldTMS ) _getDynamicProperties().get( FIELDTMS_FIELD_NAME );
        if ( tms == null ) {
        	tms = new TraitFieldTMSImpl();
            _getDynamicProperties().put( FIELDTMS_FIELD_NAME, tms );
        }
        return tms;
    }

    public void _setFieldTMS( TraitFieldTMS __$$field_Tms$$ ) {
        _getDynamicProperties().put( FIELDTMS_FIELD_NAME, __$$field_Tms$$ );
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> _getDynamicProperties() {
        return  __$$dynamic_properties_map$$;
    }

    public void _setDynamicProperties( Map map ) {
        // can't be changed
    }


    public void _setTraitMap(Map map) {
        _getDynamicProperties().put( TRAITSET_FIELD_NAME, map );
    }


    public Map<String, Thing<Map>> _getTraitMap() {
        return ( Map<String, Thing<Map>> ) _getDynamicProperties().get( TRAITSET_FIELD_NAME );
    }


    public int size() {
        return _getDynamicProperties().size();
    }

    public boolean isEmpty() {
        return _getDynamicProperties().isEmpty();
    }

    public boolean containsKey( Object key ) {
        return _getDynamicProperties().containsKey( key );
    }

    public boolean containsValue( Object value ) {
        return _getDynamicProperties().containsValue( value );
    }

    public Object get( Object key ) {
        return _getDynamicProperties().get( key );
    }

    public Object put( String key, Object value ) {
        return _getDynamicProperties().put( key, value );
    }

    public Object remove( Object key ) {
        return _getDynamicProperties().remove( key );
    }

    public void putAll( Map m ) {
        _getDynamicProperties().putAll( m );
    }

    public void clear() {
        _getDynamicProperties().clear();
    }

    public Set keySet() {
        return _getDynamicProperties().keySet();
    }

    public Collection values() {
        return _getDynamicProperties().values();
    }

    public Set<Entry<String,Object>> entrySet() {
        return _getDynamicProperties().entrySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapCore mapCore = (MapCore) o;

        if (!__$$dynamic_properties_map$$.equals(mapCore.__$$dynamic_properties_map$$)) return false;

        return true;
    }

    public int hashCode() {
        return __$$dynamic_properties_map$$.hashCode();
    }

    public void init( Map core ) {
//        __$$dynamic_properties_map$$ = core;
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

    @Override
    public String toString() {
        return "MapCore{" +
               "__$$dynamic_properties_map$$=" + __$$dynamic_properties_map$$ +
               '}';
    }
}

