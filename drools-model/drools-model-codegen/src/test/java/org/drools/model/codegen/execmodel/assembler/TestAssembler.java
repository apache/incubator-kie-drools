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
package org.drools.model.codegen.execmodel.assembler;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.drl.parser.DroolsError;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

public class TestAssembler implements KieAssemblerService {

    @Override
    public ResourceType getResourceType() {
        // we are just picking an arbitrary resource that does not have a local implementation
        return ResourceType.DMN;
    }

    @Override
    public void addResourceBeforeRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;

        kb.addBuilderResult(BEFORE_RULES);

    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        KnowledgeBuilderImpl kb = (KnowledgeBuilderImpl) kbuilder;
        PackageDescr fakEpkg = new PackageDescr("FAKEpkg");
        fakEpkg.addFunction(new FunctionDescr("INVALID", "INVALID"));
        kb.addPackage(fakEpkg);

        kb.addBuilderResult(AFTER_RULES);

    }

    public static final KnowledgeBuilderResult AFTER_RULES = new DroolsError( "AFTER_RULES") {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.WARNING;
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };

    public static final KnowledgeBuilderResult BEFORE_RULES = new DroolsError("BEFORE_RULES") {

        @Override
        public ResultSeverity getSeverity() {
            return ResultSeverity.WARNING;
        }

        @Override
        public int[] getLines() {
            return new int[0];
        }

    };
}
