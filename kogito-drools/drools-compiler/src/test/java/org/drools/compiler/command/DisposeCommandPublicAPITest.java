package org.drools.compiler.command;

import org.drools.core.WorkingMemory;
import org.drools.core.base.ClassFieldAccessorCache;
import org.drools.core.command.assertion.AssertEquals;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.core.spi.Consequence;
import org.drools.core.spi.KnowledgeHelper;
import org.junit.Assert;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.command.Command;
import org.kie.api.runtime.KieSession;
import org.kie.internal.KnowledgeBaseFactory;

public class DisposeCommandPublicAPITest {
    @Test
    public void testDisposeCommand() {

        InternalKnowledgeBase kBase;
        RuleImpl rule;
        InternalKnowledgePackage pkg;
        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();

        pkg = new KnowledgePackageImpl("org.droos.test");
        pkg.setClassFieldAccessorCache(new ClassFieldAccessorCache(Thread.currentThread().getContextClassLoader()));

        JavaDialectRuntimeData data = new JavaDialectRuntimeData();
        data.onAdd(pkg.getDialectRuntimeRegistry(), kBase.getRootClassLoader());
        pkg.getDialectRuntimeRegistry().setDialectData("java", data);
        rule = new RuleImpl("Test");
        rule.setDialect("java");
        rule.setConsequence(new Consequence() {
            public void evaluate(KnowledgeHelper knowledgeHelper, WorkingMemory workingMemory) throws Exception {

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
            Assert.assertEquals(e.getMessage(), "Illegal method call. This session was previously disposed.");

        }

    }

}
