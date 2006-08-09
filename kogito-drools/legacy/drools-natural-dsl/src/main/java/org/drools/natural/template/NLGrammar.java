package org.drools.natural.template;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

/** This represents a simple grammar mapping. */
public class NLGrammar
    implements
    Serializable {

    
    private static final long serialVersionUID = 1L;
    private List mappings = new ArrayList();
    
    public NLGrammar() {
    }
    

    /** 
     * When loading from properties, the order in which they appear in the props file
     * is the priority. (TODO: order means nothing to properties it seems).
     * 
     * Which makes sense intuitively, the order you read them it the order in which they will be applied.
     */
    public void loadFromProperties(Properties props) {
        int i = props.size();
        for ( Iterator iter = props.keySet().iterator(); iter.hasNext(); ) {
            String key = (String) iter.next();
            String property = props.getProperty(key);
            NLMappingItem item = new NLMappingItem(i, key, property);
            addNLItem(item);
            i--;
        }
    }
    
    public void addNLItem(NLMappingItem item) {
        this.mappings.add(item);
    }
    
    public Collection getMappings() {
        Collections.sort(mappings);
        return Collections.unmodifiableCollection(mappings);
    }
    
    
}
