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
package org.drools.compiler.kie.builder.impl;

/**
 * This class is intended to adapt the CompilationProblems produced by the in memory java compiler
 * to the one defined in kie-internal API
 */
public class CompilationProblemAdapter implements org.kie.internal.jci.CompilationProblem {

    private final org.kie.memorycompiler.CompilationProblem delegate;

    public CompilationProblemAdapter( org.kie.memorycompiler.CompilationProblem delegate ) {
        this.delegate = delegate;
    }

    @Override
    public boolean isError() {
        return delegate.isError();
    }

    @Override
    public String getFileName() {
        return delegate.getFileName();
    }

    @Override
    public int getStartLine() {
        return delegate.getStartLine();
    }

    @Override
    public int getStartColumn() {
        return delegate.getStartColumn();
    }

    @Override
    public int getEndLine() {
        return delegate.getEndLine();
    }

    @Override
    public int getEndColumn() {
        return delegate.getEndColumn();
    }

    @Override
    public String getMessage() {
        return delegate.getMessage();
    }

    @Override
    public String toString() {
        return getMessage();
    }
}
