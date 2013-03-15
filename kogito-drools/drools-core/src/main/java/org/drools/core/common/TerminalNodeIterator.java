package org.drools.core.common;

import java.util.Map;

import org.drools.core.util.Iterator;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.ReteooRuleBase;
import org.kie.KieBase;

public class TerminalNodeIterator
    implements
    Iterator {
    private ReteooRuleBase        ruleBase;
    private BaseNode[][]          nodes;

    private int                   i = 0;
    private int                   j = 0;
    
    TerminalNodeIterator() {
        
    }

    private TerminalNodeIterator(KieBase kbase) {
        this.ruleBase = (ReteooRuleBase) ((KnowledgeBaseImpl)kbase).ruleBase;
        Map<String, BaseNode[]> rules = ruleBase.getReteooBuilder().getTerminalNodes();
        nodes = rules.values().toArray( new BaseNode[rules.size()][] );
    }
    
    public static Iterator iterator(KieBase kbase) {
        return new TerminalNodeIterator(kbase);
    }

    public Object next() {
        if ( i >= nodes.length ) {
            return null;
        }
        BaseNode node = nodes[i][j];
        
        // now set to the next node
        j++;                
        if ( j >= nodes[i].length ) {
            i++;
            j = 0;
        }
        return node;
    }

}
