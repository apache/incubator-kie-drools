/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
package org.drools.codegen.common.context;

public class QuarkusDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Quarkus";
    public static final String QUARKUS_REST = "jakarta.ws.rs.Path";
    public static final String QUARKUS_DI = "jakarta.inject.Inject";

    protected QuarkusDroolsModelBuildContext(QuarkusKogitoBuildContextBuilder builder) {
        super(builder, /*new CDIDependencyInjectionAnnotator(), new CDIRestAnnotator(),  */ CONTEXT_NAME);
    }

    public static Builder builder() {
        return new QuarkusKogitoBuildContextBuilder();
    }

    protected static class QuarkusKogitoBuildContextBuilder extends AbstractBuilder {

        protected QuarkusKogitoBuildContextBuilder() {
        }

        @Override
        public QuarkusDroolsModelBuildContext build() {
            return new QuarkusDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return QuarkusDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}