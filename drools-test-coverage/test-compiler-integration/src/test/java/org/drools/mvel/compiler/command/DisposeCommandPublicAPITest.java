/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.mvel.compiler.command;

import org.drools.base.base.ValueResolver;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.core.rule.JavaDialectRuntimeData;
import org.drools.base.rule.consequence.Consequence;
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
