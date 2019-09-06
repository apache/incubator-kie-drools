package org.kie.kogito.rules;

import java.io.InputStream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.io.impl.InputStreamResource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.kogito.rules.impl.AbstractRuleUnit;

/**
 * A fully-runtime, reflective implementation of a rule unit, useful for testing
 */
public class InterpretedRuleUnit<T extends RuleUnitMemory> extends AbstractRuleUnit<T> {

    public static <T extends RuleUnitMemory> RuleUnit<T> of(Class<T> type) {
        return new InterpretedRuleUnit<>();
    }

    private InterpretedRuleUnit() {
        super(null);
    }

    @Override
    public RuleUnitInstance<T> createInstance(T workingMemory) {
        KnowledgeBuilder kBuilder = new KnowledgeBuilderImpl();
        Class<? extends RuleUnitMemory> wmClass = workingMemory.getClass();
        String canonicalName = wmClass.getCanonicalName();

        // transform foo.bar.Baz to /foo/bar/Baz.drl
        // this currently only works for single files
        InputStream resourceAsStream = wmClass.getResourceAsStream(
                String.format("/%s.drl", canonicalName.replace('.', '/')));
        kBuilder.add(new InputStreamResource(resourceAsStream), ResourceType.DRL);

        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kBuilder.getKnowledgePackages());
        KieSession kSession = kBase.newKieSession();

        return new InterpretedRuleUnitInstance<>(this, workingMemory, kSession);
    }
}
