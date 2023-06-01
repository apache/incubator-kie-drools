/*
 * Copyright (c) 2021. Red Hat, Inc. and/or its affiliates.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.compiler;

import org.drools.base.common.MissingDependencyException;
import org.drools.drl.parser.DroolsError;
import org.kie.api.io.Resource;

public class MissingDependencyError extends DroolsError {
    private final String message;

    public MissingDependencyError(String message) {
        this.message = message;
    }

    public MissingDependencyError(Resource resource, MissingDependencyException ex) {
        super(resource);
        message = ex.getMessage();
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
