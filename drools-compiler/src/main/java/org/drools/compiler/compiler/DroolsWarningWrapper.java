/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/

package org.drools.compiler.compiler;

import org.kie.internal.builder.KnowledgeBuilderResult;

public class DroolsWarningWrapper extends DroolsWarning {

    KnowledgeBuilderResult backingProblem;

    public DroolsWarningWrapper (KnowledgeBuilderResult problem) {
        super(problem.getResource());
        this.backingProblem = problem;
    }

    @Override
    public String getMessage() {
        return backingProblem.getMessage();
    }

    @Override
    public int[] getLines() {
        return backingProblem.getLines();
    }
}
