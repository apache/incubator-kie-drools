/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
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
