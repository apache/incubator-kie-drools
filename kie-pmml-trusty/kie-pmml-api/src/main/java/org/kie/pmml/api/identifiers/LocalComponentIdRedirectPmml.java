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
package org.kie.pmml.api.identifiers;

import org.kie.efesto.common.api.identifiers.LocalUri;

public class LocalComponentIdRedirectPmml extends AbstractModelLocalUriIdPmml {

    private static final long serialVersionUID = -4610916178245973385L;

    private final String redirectModel;

    public LocalComponentIdRedirectPmml(String redirectModel, String fileName, String name) {
        super(LocalUri.Root.append(redirectModel).append(fileName).append(name), fileName, name);
        this.redirectModel = redirectModel;
    }

    public String getRedirectModel() {
        return redirectModel;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
