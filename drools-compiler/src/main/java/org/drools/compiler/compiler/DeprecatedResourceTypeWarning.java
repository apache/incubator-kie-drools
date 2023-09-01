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
package org.drools.compiler.compiler;

import org.drools.io.InternalResource;
import org.kie.api.io.Resource;

public class DeprecatedResourceTypeWarning extends DroolsWarning {

    private final String deprecatedFormat;

    public DeprecatedResourceTypeWarning(Resource resource) {
        this(resource, resource.getResourceType().getName());
    }

    public DeprecatedResourceTypeWarning(Resource resource, String deprecatedFormat) {
        super(resource);
        this.deprecatedFormat = deprecatedFormat;
    }

    @Override
    public String getMessage() {
        return deprecatedFormat + " format usage detected. This format is deprecated and will be removed in future";
    }

    @Override
    public int[] getLines() {
        return new int[0];
    }
}
