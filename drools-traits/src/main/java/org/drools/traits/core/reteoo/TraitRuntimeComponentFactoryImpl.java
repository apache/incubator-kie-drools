package org.drools.traits.core.reteoo;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.WeakHashMap;

import org.drools.core.common.EntryPointFactory;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.base.RuleBase;
import org.drools.core.rule.accessor.FactHandleFactory;
import org.drools.kiesession.factory.RuntimeComponentFactoryImpl;
import org.drools.traits.core.common.TraitEntryPointFactory;
import org.drools.traits.core.factmodel.TraitFactoryImpl;
import org.drools.traits.core.factmodel.TraitRegistry;

public class TraitRuntimeComponentFactoryImpl extends RuntimeComponentFactoryImpl implements TraitRuntimeComponentFactory {

    private final TraitFactHandleFactory traitFactHandleFactory = new TraitFactHandleFactory();

    private final Map<RuleBase, TraitFactoryImpl> traitFactoryCache = new WeakHashMap<>(new IdentityHashMap<>());

    @Override
    public TraitFactoryImpl getTraitFactory(RuleBase knowledgeBase) {
        if (knowledgeBase instanceof SessionsAwareKnowledgeBase) {
            knowledgeBase = ((SessionsAwareKnowledgeBase) knowledgeBase).getDelegate();
        }
        return traitFactoryCache.computeIfAbsent(knowledgeBase, TraitFactoryImpl::new);
    }

    @Override
    public TraitRegistry getTraitRegistry(RuleBase knowledgeBase) {
        return getTraitFactory(knowledgeBase).getTraitRegistry();
    }

    @Override
    public FactHandleFactory getFactHandleFactoryService() {
        return traitFactHandleFactory;
    }

    @Override
    public EntryPointFactory getEntryPointFactory() {
        return new TraitEntryPointFactory();
    }

    @Override
    public int servicePriority() {
        return 1;
    }
}
