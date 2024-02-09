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
package org.drools.codegen.common.context;

public class JavaDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Java";

    protected JavaDroolsModelBuildContext(JavaKogitoBuildContextBuilder builder) {
        super(builder, null, null, CONTEXT_NAME);
    }

    public static Builder builder() {
        return new JavaKogitoBuildContextBuilder();
    }

    @Override
    public boolean hasRest() {
        return false;
    }

    @Override
    public boolean hasDI() {
        return false;
    }

    protected static class JavaKogitoBuildContextBuilder extends AbstractBuilder {

        protected JavaKogitoBuildContextBuilder() {
        }

        @Override
        public JavaDroolsModelBuildContext build() {
            return new JavaDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return JavaDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}