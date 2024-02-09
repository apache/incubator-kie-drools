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

import org.drools.codegen.common.di.impl.SpringDependencyInjectionAnnotator;
import org.drools.codegen.common.rest.impl.SpringRestAnnotator;

public class SpringBootDroolsModelBuildContext extends AbstractDroolsModelBuildContext {

    public static final String CONTEXT_NAME = "Spring";
    public static final String SPRING_REST = "org.springframework.web.bind.annotation.RestController";
    public static final String SPRING_DI = "org.springframework.beans.factory.annotation.Autowired";

    public final boolean hasRest;
    public final boolean hasDI;

    protected SpringBootDroolsModelBuildContext(SpringBootKogitoBuildContextBuilder builder) {
        super(builder, new SpringDependencyInjectionAnnotator(), new SpringRestAnnotator(), CONTEXT_NAME);
        this.hasRest = hasClassAvailable(SPRING_REST);
        this.hasDI = hasClassAvailable(SPRING_DI);
    }

    public static Builder builder() {
        return new SpringBootKogitoBuildContextBuilder();
    }

    @Override
    public boolean hasRest() {
        return hasRest;
    }

    @Override
    public boolean hasDI() {
        return hasDI;
    }

    protected static class SpringBootKogitoBuildContextBuilder extends AbstractBuilder {

        protected SpringBootKogitoBuildContextBuilder() {
        }

        @Override
        public SpringBootDroolsModelBuildContext build() {
            return new SpringBootDroolsModelBuildContext(this);
        }

        @Override
        public String toString() {
            return SpringBootDroolsModelBuildContext.CONTEXT_NAME;
        }
    }
}