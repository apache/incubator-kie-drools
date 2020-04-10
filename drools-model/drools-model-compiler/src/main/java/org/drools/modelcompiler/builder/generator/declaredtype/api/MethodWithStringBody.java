/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.drools.modelcompiler.builder.generator.declaredtype.api;

public class MethodWithStringBody implements MethodDefinition {

    private final String methodName;
    private final String returnType;

    private final String body;

    public MethodWithStringBody(String methodName, String returnType, String body) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.body = body;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getReturnType() {
        return returnType;
    }

    @Override
    public String getBody() {
        return body;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    @Override
    public boolean isPublic() {
        return true;
    }
}
