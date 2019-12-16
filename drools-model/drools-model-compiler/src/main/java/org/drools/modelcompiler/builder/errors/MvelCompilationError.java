/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.errors;

import org.drools.mvelcompiler.MvelCompilerException;
import org.kie.internal.jci.CompilationProblem;

public class MvelCompilationError implements CompilationProblem {

    MvelCompilerException exception;

    public MvelCompilationError(MvelCompilerException exception) {
        this.exception = exception;
    }

    @Override
    public boolean isError() {
        return true;
    }

    @Override
    public String getFileName() {
        return null;
    }

    @Override
    public int getStartLine() {
        return 0;
    }

    @Override
    public int getStartColumn() {
        return 0;
    }

    @Override
    public int getEndLine() {
        return 0;
    }

    @Override
    public int getEndColumn() {
        return 0;
    }

    @Override
    public String getMessage() {
        return exception.toString();
    }
}
