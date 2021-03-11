/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.dmn.core.compiler.alphanetbased;

import org.drools.core.base.ClassObjectType;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.core.rule.Declaration;
import org.drools.core.rule.Pattern;
import org.drools.model.Variable;

import static org.drools.model.DSL.declarationOf;

public class NetworkBuilderContext {

    public InternalKnowledgeBase kBase;
    public BuildContext buildContext;
    public Variable<TableContext> variable;
    public Declaration declaration;
    public ObjectTypeNode otn;

    public ResultCollector resultCollector;

    public NetworkBuilderContext(ResultCollector resultCollector) {
        kBase = (InternalKnowledgeBase) KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase);
        EntryPointNode entryPoint = buildContext.getKnowledgeBase().getRete().getEntryPointNodes().values().iterator().next();
        ClassObjectType objectType = new ClassObjectType(TableContext.class);
        variable = declarationOf(TableContext.class, "$ctx");

        Pattern pattern = new Pattern(1, objectType, "$ctx");
        declaration = pattern.getDeclaration();

        otn = new ObjectTypeNode(buildContext.getNextId(), entryPoint, objectType, buildContext);
        buildContext.setObjectSource(otn);

        this.resultCollector = resultCollector;
    }
}
