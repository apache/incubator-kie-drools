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

import org.drools.codegen.common.di.impl.CDIDependencyInjectionAnnotator;
import org.drools.codegen.common.rest.impl.CDIRestAnnotator;

public class QuarkusDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Quarkus";
    public static final String QUARKUS_REST = "jakarta.ws.rs.Path";
    public static final String QUARKUS_DI = "jakarta.inject.Inject";

    public final boolean hasRest;
    public final boolean hasDI;

    protected QuarkusDroolsModelBuildContext(QuarkusKogitoBuildContextBuilder builder) {
        super(builder, new CDIDependencyInjectionAnnotator(), new CDIRestAnnotator(), CONTEXT_NAME);
        this.hasRest = hasClassAvailable(QUARKUS_REST);
        this.hasDI = hasClassAvailable(QUARKUS_DI);
    }

    public static Builder builder() {
        return new QuarkusKogitoBuildContextBuilder();
    }

    @Override
    public boolean hasRest() {
        return hasRest;
    }

    @Override
    public boolean hasDI() {
        return hasDI;
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