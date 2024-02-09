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
package org.kie.dmn.core.compiler.alphanetbased;

import java.util.Collections;

import org.drools.base.base.ClassObjectType;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.core.reteoo.EntryPointNode;
import org.drools.core.reteoo.ObjectTypeNode;
import org.drools.core.reteoo.builder.BuildContext;
import org.drools.base.rule.Declaration;
import org.drools.base.rule.Pattern;
import org.drools.kiesession.rulebase.KnowledgeBaseFactory;
import org.drools.model.Variable;

import static org.drools.model.DSL.declarationOf;

public class ReteBuilderContext {

    public InternalKnowledgeBase kBase;
    public BuildContext buildContext;
    public Variable<PropertyEvaluator> variable;
    public Declaration declaration;
    public ObjectTypeNode otn;

    public ReteBuilderContext() {
        kBase = KnowledgeBaseFactory.newKnowledgeBase();
        buildContext = new BuildContext(kBase, Collections.emptyList());
        EntryPointNode entryPoint = buildContext.getRuleBase().getRete().getEntryPointNodes().values().iterator().next();
        ClassObjectType objectType = new ClassObjectType(PropertyEvaluator.class);
        variable = declarationOf(PropertyEvaluator.class, "$ctx");

        Pattern pattern = new Pattern(1, objectType, "$ctx");
        declaration = pattern.getDeclaration();

        otn = new ObjectTypeNode(buildContext.getNextNodeId(), entryPoint, objectType, buildContext);
        buildContext.setObjectSource(otn);
    }
}
