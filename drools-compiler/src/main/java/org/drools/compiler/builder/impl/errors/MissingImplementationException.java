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

package org.drools.compiler.builder.impl.errors;

import org.kie.api.io.Resource;

public class MissingImplementationException extends RuntimeException {

    private final Resource resource;
    private final String dependency;

    public MissingImplementationException( Resource resource, String dependency ) {
        this.resource = resource;
        this.dependency = dependency;
    }

    @Override
    public String getMessage() {
        return "Unable to compile " + resource.getSourcePath() + ". Maybe you need to add " + dependency + " to your project dependencies.";
    }
}
