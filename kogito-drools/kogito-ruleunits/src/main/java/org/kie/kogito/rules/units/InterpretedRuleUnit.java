package org.kie.kogito.rules.units;

import java.io.InputStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.InputStreamResource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.kogito.Config;
import org.kie.kogito.rules.KieRuntimeBuilder;
import org.kie.kogito.rules.RuleUnit;
import org.kie.kogito.rules.RuleUnitData;
import org.kie.kogito.rules.RuleUnitInstance;
import org.kie.kogito.rules.units.impl.AbstractRuleUnit;
import org.kie.kogito.rules.units.impl.AbstractRuleUnits;
import org.kie.kogito.uow.UnitOfWorkManager;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitData> extends AbstractRuleUnit<T> {

    public static <T extends RuleUnitData> RuleUnit<T> of(Class<T> type) {
        return new InterpretedRuleUnit<>(type.getCanonicalName());
    }

    private InterpretedRuleUnit(String id) {
        super(id, DummyApplication.INSTANCE);
    }

    @Override
    public RuleUnitInstance<T> internalCreateInstance(T data) {
        KnowledgeBuilder kBuilder = new KnowledgeBuilderImpl();
        Class<? extends RuleUnitData> wmClass = data.getClass();
        String canonicalName = wmClass.getCanonicalName();

        // transform foo.bar.Baz to /foo/bar/Baz.drl
        // this currently only works for single files
        InputStream resourceAsStream = wmClass.getResourceAsStream(
                String.format("/%s.drl", canonicalName.replace('.', '/')));
        kBuilder.add(new InputStreamResource(resourceAsStream), ResourceType.DRL);

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kBuilder.getKnowledgePackages());
        KieSession kSession = kBase.newKieSession();

        return new InterpretedRuleUnitInstance<>(this, data, kSession);
    }

    public static class DummyApplication implements org.kie.kogito.Application {

        static final DummyApplication INSTANCE = new DummyApplication();

        RuleUnits ruleUnits = new RuleUnits();

        public Config config() {
            return null;
        }

        public UnitOfWorkManager unitOfWorkManager() {
            return null;
        }

        public RuleUnits ruleUnits() {
            return ruleUnits;
        }

        public class RuleUnits extends AbstractRuleUnits {
            @Override
            protected RuleUnit<?> create( String fqcn ) {
                throw new UnsupportedOperationException();
            }

            @Override
            public KieRuntimeBuilder ruleRuntimeBuilder() {
                throw new UnsupportedOperationException();
            }
        }
    }
}
