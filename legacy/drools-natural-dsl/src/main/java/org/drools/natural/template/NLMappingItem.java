package org.drools.natural.template;

import java.io.Serializable;

/**
 * This contains a single mapping from psuedo NL to a grammarTemplate.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class NLMappingItem
    implements
    Comparable, Serializable {


    private static final long serialVersionUID = 7185580607729787497L;
    
    private int priority = 0;
    
    private String naturalTemplate;
    private String grammarTemplate;
    
    public NLMappingItem(int priority,
                         String naturalTemplate,
                         String grammarTemplate) {
        this.priority = priority;
        this.naturalTemplate = naturalTemplate;
        this.grammarTemplate = grammarTemplate;
    }
    
    public String getNaturalTemplate() {
        return naturalTemplate;
    }

    public String getGrammarTemplate() {
        return grammarTemplate;
    }

    public int compareTo(Object arg) {
        if ( arg instanceof NLMappingItem ) {
            NLMappingItem item = (NLMappingItem) arg;
            if ( item.priority == this.priority ) return 0;
            if ( item.priority > this.priority ) return -1;
            if ( item.priority < this.priority ) return 1;
            return 0;
        }
        else {
            return 0;
        }
    }

    int getPriority() {
        return this.priority;
    }

    
    
}
