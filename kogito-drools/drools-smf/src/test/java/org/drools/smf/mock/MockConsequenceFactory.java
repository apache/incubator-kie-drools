package org.drools.smf.mock;

import org.drools.rule.Rule;
import org.drools.smf.Configuration;
import org.drools.smf.ConsequenceFactory;
import org.drools.smf.FactoryException;
import org.drools.spi.Consequence;
import org.drools.spi.RuleBaseContext;

public class MockConsequenceFactory
    implements
    ConsequenceFactory {

    public Consequence newConsequence(Rule rule,
                                      RuleBaseContext context,
                                      Configuration config) throws FactoryException {
        
        return null;
    }

}
