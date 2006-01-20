package org.drools.smf.mock;

import org.drools.rule.RuleSet;
import org.drools.smf.Configuration;
import org.drools.smf.FactoryException;
import org.drools.smf.FunctionsFactory;
import org.drools.spi.Functions;
import org.drools.spi.RuleBaseContext;

public class MockFunctionsFactory
    implements
    FunctionsFactory {

    public Functions newFunctions(RuleSet ruleSet,
                                  RuleBaseContext context,
                                  Configuration config) throws FactoryException {       
        return null;
    }

}
