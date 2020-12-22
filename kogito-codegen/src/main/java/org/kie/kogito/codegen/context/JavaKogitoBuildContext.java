/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import org.kie.kogito.codegen.AddonsConfig;

import java.io.File;
import java.util.Properties;
import java.util.function.Predicate;

public class JavaKogitoBuildContext extends AbstractKogitoBuildContext {

    protected JavaKogitoBuildContext(String packageName, Predicate<String> classAvailabilityResolver, File targetDirectory, AddonsConfig addonsConfig, Properties applicationProperties) {
        super(packageName, classAvailabilityResolver, null, targetDirectory, addonsConfig, applicationProperties);
    }

    public static Builder builder() {
        return new JavaKogitoBuildContextBuilder();
    }

    protected static class JavaKogitoBuildContextBuilder extends AbstractBuilder {

        protected JavaKogitoBuildContextBuilder() {
        }

        @Override
        public JavaKogitoBuildContext build() {
            return new JavaKogitoBuildContext(packageName, classAvailabilityResolver, targetDirectory, addonsConfig, applicationProperties);
        }
    }
}