package org.drools.verifier;

import org.drools.verifier.misc.RuleLoader;

public class DefaultVerifierConfiguration extends VerifierConfigurationImpl {

    public DefaultVerifierConfiguration() {
        verifyingResources.putAll( RuleLoader.basicRulesForFullKnowledgeBase() );
    }

}
