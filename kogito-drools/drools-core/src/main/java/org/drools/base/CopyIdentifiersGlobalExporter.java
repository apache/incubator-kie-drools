/**
 * 
 */
package org.drools.base;

import java.util.HashMap;
import java.util.Map;

import org.drools.WorkingMemory;
import org.drools.common.InternalRuleBase;
import org.drools.spi.GlobalExporter;
import org.drools.spi.GlobalResolver;

/**
 * Creates a new GlobalResolver consisting of just the identifiers specified in the String[].
 * If the String[] is null, or the default constructor is used, then all globals defined in the RuleBase
 * will be copied.
 *
 */
public class CopyIdentifiersGlobalExporter implements GlobalExporter {
    private String[] identifiers;
    
    /**
     * All identifiers will be copied
     *
     */
    public CopyIdentifiersGlobalExporter() {
        this.identifiers = null;
    }
    
    /**
     * Specified identifiers will be copied
     * @param identifiers
     */
    public CopyIdentifiersGlobalExporter(String[] identifiers) {
        this.identifiers = identifiers;
    }
    
    public GlobalResolver export(WorkingMemory workingMemory) {
        if ( this.identifiers == null || this.identifiers.length == 0 ) {
            // no identifiers, to get all the identifiers from that defined in
            // the rulebase
            Map map = ((InternalRuleBase)workingMemory.getRuleBase()).getGlobals();
            this.identifiers = new String[ map.size() ];
            this.identifiers = (String[]) map.keySet().toArray( this.identifiers );
        }
        
        Map map = new HashMap(identifiers.length);
        for ( int i = 0, length = identifiers.length; i < length; i++ ) {
            map.put( identifiers[i], workingMemory.getGlobal( identifiers[i] ) );
        }
        return new MapGlobalResolver(map);
    }
}