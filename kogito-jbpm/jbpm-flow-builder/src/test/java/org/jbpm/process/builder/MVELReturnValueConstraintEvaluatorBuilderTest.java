/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.process.builder;

import java.io.StringReader;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.ReturnValueDescr;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELDialect;
import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.process.builder.dialect.mvel.MVELReturnValueEvaluatorBuilder;
import org.jbpm.process.instance.impl.MVELReturnValueEvaluator;
import org.jbpm.process.instance.impl.ReturnValueConstraintEvaluator;
import org.jbpm.ruleflow.instance.RuleFlowProcessInstance;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.instance.node.SplitInstance;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.assertj.core.api.Assertions.assertThat;

public class MVELReturnValueConstraintEvaluatorBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleReturnValueConstraintEvaluator() throws Exception {
        final InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage("pkg1");

        ReturnValueDescr descr = new ReturnValueDescr();
        descr.setText("return value");

        builder = new KnowledgeBuilderImpl(pkg, new SemanticKnowledgeBuilderConfigurationImpl());
        DialectCompiletimeRegistry dialectRegistry = builder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect("mvel");

        PackageBuildContext context = new PackageBuildContext();
        context.initContext(builder,
                pkg,
                null,
                dialectRegistry,
                mvelDialect,
                null);

        builder.addPackageFromDrl(new StringReader("package pkg1;\nglobal Boolean value;"));

        ReturnValueConstraintEvaluator node = new ReturnValueConstraintEvaluator();

        final MVELReturnValueEvaluatorBuilder evaluatorBuilder = new MVELReturnValueEvaluatorBuilder();
        evaluatorBuilder.build(context,
                node,
                descr,
                null);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        kruntime.getKieSession().setGlobal("value", true);

        RuleFlowProcessInstance processInstance = new RuleFlowProcessInstance();
        processInstance.setKnowledgeRuntime((InternalKnowledgeRuntime) kruntime.getKieSession());

        SplitInstance splitInstance = new SplitInstance();
        splitInstance.setProcessInstance(processInstance);

        MVELDialectRuntimeData data = (MVELDialectRuntimeData) builder.getPackage("pkg1").getDialectRuntimeRegistry().getDialectData("mvel");

        ((MVELReturnValueEvaluator) node.getReturnValueEvaluator()).compile(data);

        assertThat(node.evaluate(splitInstance,
                null,
                null)).isTrue();

        kruntime.getKieSession().setGlobal("value", false);

        assertThat(node.evaluate(splitInstance,
                null,
                null)).isFalse();
    }

}
