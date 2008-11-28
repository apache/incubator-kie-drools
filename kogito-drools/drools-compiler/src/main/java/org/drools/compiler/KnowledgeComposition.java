package org.drools.compiler;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeComposition {
    private List<KnowledgeResource> resources;
    
    public KnowledgeComposition() {
        this.resources = new ArrayList<KnowledgeResource>();
    }

    public List<KnowledgeResource> getResources() {
        return resources;
    }

    public void setResources(List<KnowledgeResource> parts) {
        this.resources = parts;
    }
               
}
