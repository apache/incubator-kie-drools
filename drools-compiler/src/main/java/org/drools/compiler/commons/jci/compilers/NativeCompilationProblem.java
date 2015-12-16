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

package org.drools.compiler.commons.jci.compilers;

import org.drools.compiler.commons.jci.problems.CompilationProblem;

import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

public class NativeCompilationProblem implements CompilationProblem {

    private final Diagnostic<JavaFileObject> problem;

    public NativeCompilationProblem(Diagnostic<JavaFileObject> problem) {
        this.problem = problem;
    }

    public boolean isError() {
        return problem.getKind() == Diagnostic.Kind.ERROR;
    }

    public String getFileName() {
        return problem.getSource().getName().substring(1);
    }

    public int getStartLine() {
        return (int)problem.getLineNumber();
    }

    public int getStartColumn() {
        return (int)problem.getColumnNumber();
    }

    public int getEndLine() {
        return (int)problem.getLineNumber();
    }

    public int getEndColumn() {
        return (int)problem.getColumnNumber();
    }

    public String getMessage() {
        return problem.getMessage(null);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getFileName()).append(" (");
        sb.append(getStartLine());
        sb.append(":");
        sb.append(getStartColumn());
        sb.append(") : ");
        sb.append(getMessage());
        return sb.toString();
    }
}
