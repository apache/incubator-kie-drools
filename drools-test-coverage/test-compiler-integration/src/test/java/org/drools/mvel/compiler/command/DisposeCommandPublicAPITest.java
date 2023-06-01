package org.drools.mvel.compiler.command;

import org.drools.base.base.ValueResolver;
import org.drools.core.common.ReteEvaluator;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.rule.consequence.Consequence;
import org.drools.core.rule.consequence.KnowledgeHelper;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;

import static org.assertj.core.api.Assertions.assertThat;

public class DisposeCommandPublicAPITest {
    @Test
    public void testDisposeCommand() {

        InternalKnowledgeBase kBase;
        RuleImpl rule;
        InternalKnowledgePackage pkg;
        kBase = KnowledgeBaseFactory.newKnowledgeBase();

        pkg = CoreComponentFactory.get().createKnowledgePackage("org.droos.test");
        pkg.setClassLoader(Thread.currentThread().getContextClassLoader());

        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd(pkg.getDialectRuntimeRegistry(), kBase.getRootClassLoader());
        pkg.getDialectRuntimeRegistry().setDialectData("java", data);
        rule = new RuleImpl("Test");
        rule.setDialect("java");
        rule.setConsequence(new Consequence<KnowledgeHelper>() {
            public void evaluate(KnowledgeHelper knowledgeHelper, ValueResolver valueResolver) throws Exception {

            }

            public String getName() {
                return "default";
            }
        });
        pkg.addRule(rule);

        kBase.addPackage(pkg);
        KieSession session = kBase.newKieSession();
        Command dispose = KieServices.Factory.get().getCommands().newDispose();
        session.insert("whatever");
        session.fireAllRules();
        session.execute(dispose);
        try {
            session.insert("whatever");
        } catch (Exception e) {
            assertThat("Illegal method call. This session was previously disposed.").isEqualTo(e.getMessage());

        }

    }

}
