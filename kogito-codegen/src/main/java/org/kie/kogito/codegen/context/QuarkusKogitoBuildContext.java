/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.kogito.codegen.context;

import org.kie.kogito.codegen.di.CDIDependencyInjectionAnnotator;

public class QuarkusKogitoBuildContext extends AbstractKogitoBuildContext {

    public static final String CONTEXT_NAME = "Quarkus";

    protected QuarkusKogitoBuildContext(QuarkusKogitoBuildContextBuilder builder) {
        super(builder, new CDIDependencyInjectionAnnotator(), CONTEXT_NAME);
    }

    @Override
    public boolean hasREST() {
        return hasClassAvailable("javax.ws.rs.Path");
    }

    public static Builder builder() {
        return new QuarkusKogitoBuildContextBuilder();
    }

    protected static class QuarkusKogitoBuildContextBuilder extends AbstractBuilder {

        protected QuarkusKogitoBuildContextBuilder() {
        }

        @Override
        public QuarkusKogitoBuildContext build() {
            return new QuarkusKogitoBuildContext(this);
        }

        @Override
        public String toString() {
            return "Quarkus";
        }
    }
}