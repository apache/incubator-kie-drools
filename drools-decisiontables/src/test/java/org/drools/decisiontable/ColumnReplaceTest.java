package org.drools.decisiontable;

import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.DecisionTableConfiguration;
import org.kie.internal.builder.DecisionTableInputType;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

public class ColumnReplaceTest {

    @Test
    public void testAutoFocusToLockOnActiveReplacement(){
        
        DecisionTableConfiguration dTableConfiguration = KnowledgeBuilderFactory.newDecisionTableConfiguration();
        dTableConfiguration.setInputType( DecisionTableInputType.CSV );

        KnowledgeBuilder kbuilder = KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilder.add(ResourceFactory.newClassPathResource("columnReplaceTest.drl.csv", getClass()), ResourceType.DTABLE, dTableConfiguration);
        if (kbuilder.hasErrors())
        {
            System.out.println(kbuilder.getErrors());
            fail("Knowledge builder cannot compile package!");
        }

        InternalKnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
        kbase.addPackages(kbuilder.getKnowledgePackages());

        assertThat(((RuleImpl) kbase.getRule("org.drools.decisiontable", "lockOnActiveRule")).isLockOnActive()).isTrue();
        // lock-on-active was not set on autoFocusRule, so it should be by default false
        assertThat(((RuleImpl) kbase.getRule("org.drools.decisiontable", "autoFocusRule")).isLockOnActive()).isFalse();

        assertThat(((RuleImpl) kbase.getRule("org.drools.decisiontable", "lockOnActiveRule")).getAutoFocus()).isFalse();
        // auto-focus was set to be true, so it should be true
        assertThat(((RuleImpl) kbase.getRule("org.drools.decisiontable", "autoFocusRule")).getAutoFocus()).isTrue();
    }
}
