package org.drools.traits.compiler;

import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.traits.core.base.evaluators.IsAEvaluatorDefinition;
import org.junit.BeforeClass;
import org.kie.api.KieBase;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.builder.conf.EvaluatorOption;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.fail;

public class CommonTraitTest {

    @BeforeClass
    public static void beforeClass() {
        System.setProperty(EvaluatorOption.PROPERTY_NAME + "isA", IsAEvaluatorDefinition.class.getName());
    }

    protected KieBase loadKnowledgeBaseFromString(String... drlContentStrings) {
        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        for (String drlContentString : drlContentStrings) {
            kbuilder.add(ResourceFactory.newByteArrayResource(drlContentString
                                                                      .getBytes()), ResourceType.DRL);
        }

        if (kbuilder.hasErrors()) {
            fail(kbuilder.getErrors().toString());
        }
        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());
        return kbase;
    }
}
