/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.compiler.builder.impl.errors;

import org.drools.compiler.compiler.DroolsError;
import org.drools.compiler.compiler.FunctionError;
import org.drools.compiler.lang.descr.FunctionDescr;

public class FunctionErrorHandler extends ErrorHandler {

    private FunctionDescr descr;

    public FunctionErrorHandler(final FunctionDescr functionDescr,
                                final String message) {
        this.descr = functionDescr;
        this.message = message;
    }

    public DroolsError getError() {
        return new FunctionError(this.descr,
                                 collectCompilerProblems(),
                                 this.message);
    }

}
