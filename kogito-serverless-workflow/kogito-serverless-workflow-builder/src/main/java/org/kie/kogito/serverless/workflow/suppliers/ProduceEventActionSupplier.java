/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.serverless.workflow.suppliers;

import org.jbpm.compiler.canonical.AbstractNodeVisitor;
import org.jbpm.compiler.canonical.ExpressionSupplier;
import org.jbpm.compiler.canonical.ProcessMetaData;
import org.jbpm.compiler.canonical.TriggerMetaData;
import org.jbpm.process.instance.impl.Action;
import org.kie.kogito.internal.process.runtime.KogitoNode;
import org.kie.kogito.internal.process.runtime.KogitoProcessContext;
import org.kie.kogito.serverless.workflow.actions.SWFProduceEventAction;
import org.slf4j.LoggerFactory;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.NullLiteralExpr;
import com.github.javaparser.ast.expr.StringLiteralExpr;

import static com.github.javaparser.StaticJavaParser.parseClassOrInterfaceType;

public class ProduceEventActionSupplier implements ExpressionSupplier, Action {

    private final String exprLang;
    private final String data;

    public ProduceEventActionSupplier(String exprLang, String data) {
        this.exprLang = exprLang;
        this.data = data;
    }

    @Override
    public Expression get(KogitoNode node, ProcessMetaData metadata) {
        return AbstractNodeVisitor.buildProducerAction(parseClassOrInterfaceType(SWFProduceEventAction.class.getCanonicalName()), TriggerMetaData.of(node), metadata)
                .addArgument(new StringLiteralExpr(exprLang))
                .addArgument(data != null ? new StringLiteralExpr().setString(data) : new NullLiteralExpr());
    }

    @Override
    public void execute(KogitoProcessContext context) throws Exception {
        LoggerFactory.getLogger(ProduceEventActionSupplier.class).warn("Code generation step is needed for event production");
    }

}
