
package org.drools.xml;

import java.util.HashMap;
import java.util.Map;

public class SemanticModules {
    public Map<String, SemanticModule> modules;

    public SemanticModules() {
        this.modules = new HashMap<String, SemanticModule>();
    }

    public void addSemanticModule(SemanticModule module) {
        this.modules.put( module.getUri(),
                          module );
    }

    public SemanticModule getSemanticModule(String uri) {
        return this.modules.get( uri );
    }
    
    public String toString() {
        return this.modules.toString();
    }
}
