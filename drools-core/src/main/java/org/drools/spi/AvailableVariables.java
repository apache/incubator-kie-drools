package org.drools.spi;

import java.util.Map;

import org.drools.rule.Declaration;

public class AvailableVariables {
    private Map[] maps;
    
    public AvailableVariables(Map[] maps) {
        this.maps = maps;
    }
    
    public Class getType(String name) {
        for ( int i = 0, length = maps.length; i < length; i++ ) {
            Object object = maps[i].get( name );
            if ( object != null ) {            
                if ( object.getClass() == Declaration.class ) {
                    return ( ( Declaration ) object ).getExtractor().getExtractToClass();
                } else {
                    return ( Class ) object;
                }
            }            
        }    
        return null;
    }
    
    public boolean available(String name) {
        for ( int i = 0, length = maps.length; i < length; i++ ) {
            if ( maps[i].containsKey( ( name ) ) ) {
                return true;
            }
        }
        return false;
    }
}
