package org.drools.serialization.protobuf.iterators;

import java.util.Map;

import org.drools.core.impl.InternalRuleBase;
import org.drools.core.reteoo.TerminalNode;
import org.drools.core.util.Iterator;
import org.kie.api.KieBase;

public class TerminalNodeIterator implements Iterator {
    private InternalRuleBase kBase;
    private TerminalNode[][] nodes;

    private int i = 0;
    private int j = 0;
    
    TerminalNodeIterator() {
        
    }

    private TerminalNodeIterator(KieBase kBase) {
        this.kBase = (InternalRuleBase)kBase;
        Map<String, TerminalNode[]> rules = this.kBase.getReteooBuilder().getTerminalNodes();
        nodes = rules.values().toArray( new TerminalNode[rules.size()][] );
    }
    
    public static Iterator iterator(KieBase kBase) {
        return new TerminalNodeIterator(kBase);
    }

    public Object next() {
        if ( i >= nodes.length ) {
            return null;
        }
        TerminalNode node = nodes[i][j];
        
        // now set to the next node
        j++;                
        if ( j >= nodes[i].length ) {
            i++;
            j = 0;
        }
        return node;
    }

}
