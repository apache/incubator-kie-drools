package org.drools.lang.dsl.template;

import java.io.Serializable;

/**
 * This contains a single mapping from psuedo NL to a grammarTemplate.
 * 
 * @author <a href="mailto:michael.neale@gmail.com"> Michael Neale</a>
 *
 */
public class NLMappingItem
    implements
    Serializable {


    private static final long serialVersionUID = 7185580607729787497L;
    
    private String naturalTemplate;
    private String targetTemplate;
    private String scope;
    

    
    public NLMappingItem(String naturalTemplate,
                         String targetTemplate,
                         String scope) {
        this.naturalTemplate = naturalTemplate;
        this.targetTemplate = targetTemplate;        
        this.scope = scope;        
    }
    
    public String getNaturalTemplate() {
        return naturalTemplate;
    }

    public String getTargetTemplate() {
        return targetTemplate;
    }


    public String getScope() {
        return this.scope;
    }
    
}
