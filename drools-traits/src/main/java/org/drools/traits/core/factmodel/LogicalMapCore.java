package org.drools.traits.core.factmodel;

import org.drools.base.factmodel.traits.TraitFieldTMS;
import org.drools.base.factmodel.traits.Traitable;

import java.util.Map;

@Traitable( logical=true )
public class LogicalMapCore<K> extends MapCore {


    public LogicalMapCore( Map core ) {
        super( core );

        TraitFieldTMS tms = _getFieldTMS();
        Map<String,Object> props = _getDynamicProperties();
        for ( Entry<String,Object> propEntry : props.entrySet() ) {
            tms.registerField( Map.class, propEntry.getKey(), Object.class, propEntry.getValue(), null );
        }
    }
}
