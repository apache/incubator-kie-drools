package org.drools.ruleunits.impl.factory;

import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.kiesession.factory.WorkingMemoryFactory;
import org.drools.ruleunits.impl.facthandles.RuleUnitFactHandleFactory;

public class RuleUnitRuntimeComponentFactory extends RuntimeComponentFactoryImpl {

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return new RuleUnitFactHandleFactory();
    }

    private WorkingMemoryFactory wmFactory = RuleUnitPhreakWorkingMemoryFactory.getInstance();

    @Override
    public WorkingMemoryFactory getWorkingMemoryFactory() {
        return wmFactory;
    }

    @Override
    public int servicePriority() {
        return 2;
    }
}
