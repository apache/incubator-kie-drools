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
 *
 */

package org.drools.compiler.kproject.xml;

import org.kie.api.builder.ReleaseId;

public interface DependencyFilter {
    boolean accept(ReleaseId releaseId, String scope);

    DependencyFilter TAKE_ALL_FILTER = new DependencyFilter() {
        @Override
        public boolean accept( ReleaseId releaseId, String scope ) {
            return true;
        }
    };

    DependencyFilter COMPILE_FILTER = new ExcludeScopeFilter("test", "provided");

    class ExcludeScopeFilter implements DependencyFilter {
        private final String[] excludedScopes;

        public ExcludeScopeFilter( String... excludedScopes ) {
            this.excludedScopes = excludedScopes;
        }

        @Override
        public boolean accept( ReleaseId releaseId, String scope ) {
            for (String excludedScope : excludedScopes) {
                if (excludedScope.equals( scope )) {
                    return false;
                }
            }
            return true;
        }
    }
}
