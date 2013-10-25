package org.drools.core.factmodel.traits;

import org.drools.core.factmodel.MapCore;

import java.util.Map;

@Traitable( logical=true )
public class LogicalMapCore<K> extends MapCore<K> {



    public LogicalMapCore( Map core ) {
        super( core );

        for ( String key : this._getDynamicProperties().keySet() ) {
            this._getFieldTMS().registerField( Map.class, key, Object.class, this._getDynamicProperties().get( key ), null );
        }
    }
}
