/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.lang.descr.ProcessDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.spi.KogitoProcessContextImpl;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.builder.dialect.javascript.JavaScriptActionBuilder;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.impl.WorkflowProcessImpl;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class JavaScriptActionBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleAction() throws Exception {
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("pkg1");

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText("var testString; print('Hello')");

        builder.addPackage(pkg);
        DialectCompiletimeRegistry dialectRegistry = builder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName("Process1");
        processDescr.setName("Process1");

        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName("Process1");
        process.setPackageName("pkg1");

        ProcessBuildContext context = new ProcessBuildContext(builder, builder.getPackage("pkg1"), null, processDescr, dialectRegistry, null);
        context.init(builder, pkg, null, dialectRegistry, null, null);

        builder.addPackageFromDrl(new StringReader("package pkg1;\nglobal String testField;\n"));

        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("JavaScript", null);
        actionNode.setAction(action);

        ProcessDialect dialect = ProcessDialectRegistry.getDialect("JavaScript");
        dialect.getActionBuilder().build(context, action, actionDescr, actionNode);
        dialect.addProcess(context);

        final JavaScriptActionBuilder builder = new JavaScriptActionBuilder();
        builder.build(context,
                action,
                actionDescr,
                actionNode);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        kruntime.getKieSession().setGlobal("testField", "vagon");

        KogitoProcessContext processContext = new KogitoProcessContextImpl(kruntime.getKieRuntime());
        ((Action) actionNode.getAction().getMetaData("Action")).execute(processContext);

        assertEquals("vagon", kruntime.getKieSession().getGlobal("testField").toString());
    }
}
