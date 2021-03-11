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

package org.drools.modelcompiler.util.lambdareplace;

import java.util.Objects;

import com.github.javaparser.ast.CompilationUnit;

import static org.drools.modelcompiler.util.lambdareplace.ExecModelLambdaPostProcessor.MATERIALIZED_LAMBDA_PRETTY_PRINTER;

public class CreatedClass {

    private final CompilationUnit compilationUnit;
    private final String className;
    private final String packageName;

    public CreatedClass(CompilationUnit compilationUnit, String className, String packageName) {
        this.compilationUnit = compilationUnit;
        this.className = className;
        this.packageName = packageName;
    }

    public String getCompilationUnitAsString() {
        return MATERIALIZED_LAMBDA_PRETTY_PRINTER.print(compilationUnit);
    }

    public String getClassNameWithPackage() {
        return String.format("%s.%s", packageName, className);
    }

    public String getClassNamePath() {
        return String.format("%s/%s.java", packageName.replace(".", "/"), className);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CreatedClass that = (CreatedClass) o;
        return compilationUnit.equals(that.compilationUnit) &&
                className.equals(that.className) &&
                packageName.equals(that.packageName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compilationUnit, className, packageName);
    }

    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }
}
