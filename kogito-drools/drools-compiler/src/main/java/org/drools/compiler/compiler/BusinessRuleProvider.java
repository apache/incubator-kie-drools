package org.drools.compiler.compiler;

import java.io.IOException;
import java.io.Reader;

import org.kie.io.Resource;

public interface BusinessRuleProvider {

    public Reader getKnowledgeReader(Resource ruleResource) throws IOException;

    public boolean hasDSLSentences();
    
}
