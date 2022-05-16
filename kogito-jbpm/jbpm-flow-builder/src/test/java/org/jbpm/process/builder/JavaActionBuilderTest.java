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
import java.util.ArrayList;
import java.util.List;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.reteoo.CoreComponentFactory;
import org.drools.drl.ast.descr.ActionDescr;
import org.drools.drl.ast.descr.ProcessDescr;
import org.drools.mvel.java.JavaDialect;
import org.jbpm.compiler.xml.compiler.SemanticKnowledgeBuilderConfigurationImpl;
import org.jbpm.process.builder.dialect.ProcessDialect;
import org.jbpm.process.builder.dialect.ProcessDialectRegistry;
import org.jbpm.process.instance.KogitoProcessContextImpl;
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

public class JavaActionBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleAction() throws Exception {
        final InternalKnowledgePackage pkg = CoreComponentFactory.get().createKnowledgePackage("pkg1");

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText("list.add( \"hello world\" );");

        builder = new KnowledgeBuilderImpl(pkg, new SemanticKnowledgeBuilderConfigurationImpl());
        DialectCompiletimeRegistry dialectRegistry = builder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        JavaDialect javaDialect = (JavaDialect) dialectRegistry.getDialect("java");

        ProcessDescr processDescr = new ProcessDescr();
        processDescr.setClassName("Process1");
        processDescr.setName("Process1");

        WorkflowProcessImpl process = new WorkflowProcessImpl();
        process.setName("Process1");
        process.setPackageName("pkg1");

        ProcessBuildContext context = new ProcessBuildContext(builder, builder.getPackage("pkg1"), null, processDescr, dialectRegistry, javaDialect);

        context.init(builder, pkg, null, dialectRegistry, javaDialect, null);

        builder.addPackageFromDrl(new StringReader("package pkg1;\nglobal java.util.List list;\n"));

        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("java", null);
        actionNode.setAction(action);

        ProcessDialect dialect = ProcessDialectRegistry.getDialect("java");
        dialect.getActionBuilder().build(context, action, actionDescr, actionNode);
        dialect.addProcess(context);
        javaDialect.compileAll();
        assertEquals(0, javaDialect.getResults().size());

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);

        KogitoProcessContext processContext = new KogitoProcessContextImpl(kruntime.getKieRuntime());
        ((Action) actionNode.getAction().getMetaData("Action")).execute(processContext);

        assertEquals("hello world", list.get(0));
    }

}
