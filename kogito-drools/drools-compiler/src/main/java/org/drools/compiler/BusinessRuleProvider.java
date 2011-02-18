package org.drools.compiler;

import java.io.IOException;
import java.io.Reader;

import org.drools.io.Resource;

public interface BusinessRuleProvider {

    public Reader getKnowledgeReader(Resource ruleResource) throws IOException;

}
