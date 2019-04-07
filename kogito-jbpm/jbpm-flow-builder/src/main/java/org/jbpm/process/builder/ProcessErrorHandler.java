/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jbpm.process.builder;

import org.drools.compiler.builder.impl.errors.ErrorHandler;
import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.lang.descr.BaseDescr;
import org.jbpm.compiler.ProcessBuildError;
import org.kie.api.definition.process.Process;

public class ProcessErrorHandler extends ErrorHandler {

    private BaseDescr descr;

    private Process   process;

    public ProcessErrorHandler(final BaseDescr ruleDescr,
                               final Process process,
                               final String message) {
        this.descr = ruleDescr;
        this.process = process;
        this.message = message;
    }

    public DroolsError getError() {
        return new ProcessBuildError( this.process,
                                      this.descr,
                                      collectCompilerProblems(),
                                      this.message );
    }

}
