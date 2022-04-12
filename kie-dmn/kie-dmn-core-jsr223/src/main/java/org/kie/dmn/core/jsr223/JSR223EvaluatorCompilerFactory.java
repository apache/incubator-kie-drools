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

package org.kie.dmn.core.jsr223;

import org.kie.dmn.core.compiler.DMNCompilerConfigurationImpl;
import org.kie.dmn.core.compiler.DMNCompilerImpl;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompiler;
import org.kie.dmn.core.compiler.DMNDecisionLogicCompilerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JSR223EvaluatorCompilerFactory implements DMNDecisionLogicCompilerFactory {
    private static final Logger LOG = LoggerFactory.getLogger( JSR223EvaluatorCompilerFactory.class );

    @Override
    public DMNDecisionLogicCompiler newDMNDecisionLogicCompiler(DMNCompilerImpl dmnCompiler, DMNCompilerConfigurationImpl dmnCompilerConfig) {
        LOG.info("Instantiating JSR223EvaluatorCompilerFactory");
        return new JSR223EvaluatorCompiler(dmnCompiler);
    }

}