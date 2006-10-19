package org.drools.spi;

import java.util.Map;

import org.drools.rule.Declaration;

public class AvailableVariables {
    private Map[] maps;

    public AvailableVariables(final Map[] maps) {
        this.maps = maps;
    }

    public Class getType(final String name) {
        for ( int i = 0, length = this.maps.length; i < length; i++ ) {
            final Object object = this.maps[i].get( name );
            if ( object != null ) {
                if ( object.getClass() == Declaration.class ) {
                    return ((Declaration) object).getExtractor().getExtractToClass();
                } else {
                    return (Class) object;
                }
            }
        }
        return null;
    }

    public boolean available(final String name) {
        for ( int i = 0, length = this.maps.length; i < length; i++ ) {
            if ( this.maps[i].containsKey( (name) ) ) {
                return true;
            }
        }
        return false;
    }
}
