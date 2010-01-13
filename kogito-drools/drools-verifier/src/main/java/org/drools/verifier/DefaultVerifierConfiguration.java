package org.drools.verifier;

import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;

public class DefaultVerifierConfiguration extends VerifierConfigurationImpl {

    public DefaultVerifierConfiguration() {
        verifyingResources.put( ResourceFactory.newClassPathResource( "scope-knowledge-package.xml",
                                                                      getClass() ),
                                ResourceType.CHANGE_SET );
        //        verifyingResources.putAll( RuleLoader.basicRulesForFullKnowledgeBase( getClass() ) );
        verifyingScopes.add( VERIFYING_SCOPE_SINGLE_RULE );
        verifyingScopes.add( VERIFYING_SCOPE_DECISION_TABLE );
        verifyingScopes.add( VERIFYING_SCOPE_KNOWLEDGE_PACKAGE );
    }

}
