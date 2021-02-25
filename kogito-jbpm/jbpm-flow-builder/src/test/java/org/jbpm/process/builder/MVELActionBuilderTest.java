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
import org.drools.compiler.lang.descr.ActionDescr;
import org.drools.compiler.rule.builder.PackageBuildContext;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.spi.KogitoProcessContextImpl;
import org.drools.mvel.MVELDialectRuntimeData;
import org.drools.mvel.builder.MVELDialect;
import org.jbpm.process.builder.dialect.mvel.MVELActionBuilder;
import org.jbpm.process.instance.impl.Action;
import org.jbpm.process.instance.impl.MVELAction;
import org.jbpm.test.util.AbstractBaseTest;
import org.jbpm.workflow.core.DroolsAction;
import org.jbpm.workflow.core.impl.DroolsConsequenceAction;
import org.jbpm.workflow.core.node.ActionNode;
import org.junit.jupiter.api.Test;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MVELActionBuilderTest extends AbstractBaseTest {

    @Test
    public void testSimpleAction() throws Exception {
        final InternalKnowledgePackage pkg = new KnowledgePackageImpl("pkg1");

        ActionDescr actionDescr = new ActionDescr();
        actionDescr.setText("list.add( 'hello world' )");

        builder = new KnowledgeBuilderImpl(pkg);
        DialectCompiletimeRegistry dialectRegistry = builder.getPackageRegistry(pkg.getName()).getDialectCompiletimeRegistry();
        MVELDialect mvelDialect = (MVELDialect) dialectRegistry.getDialect("mvel");

        PackageBuildContext context = new PackageBuildContext();
        context.init(builder, pkg, null, dialectRegistry, mvelDialect, null);

        builder.addPackageFromDrl(new StringReader("package pkg1;\nglobal java.util.List list;\n"));

        ActionNode actionNode = new ActionNode();
        DroolsAction action = new DroolsConsequenceAction("mvel", null);
        actionNode.setAction(action);

        final MVELActionBuilder actionBuilder = new MVELActionBuilder();
        actionBuilder.build(context,
                action,
                actionDescr,
                actionNode);

        KogitoProcessRuntime kruntime = createKogitoProcessRuntime();

        List<String> list = new ArrayList<String>();
        kruntime.getKieSession().setGlobal("list", list);

        MVELDialectRuntimeData data = (MVELDialectRuntimeData) builder.getPackage("pkg1").getDialectRuntimeRegistry().getDialectData("mvel");

        ((MVELAction) actionNode.getAction().getMetaData("Action")).compile(data);

        KogitoProcessContext processContext = new KogitoProcessContextImpl(kruntime.getKieRuntime());
        ((Action) actionNode.getAction().getMetaData("Action")).execute(processContext);

        assertEquals("hello world", list.get(0));
    }

}
