/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

package org.kie.dmn.model.v1_3;

import org.kie.dmn.model.api.Import;

public class TImport extends TNamedElement implements Import {

    protected String namespace;
    protected String locationURI;
    protected String importType;

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String value) {
        this.namespace = value;
    }

    @Override
    public String getLocationURI() {
        return locationURI;
    }

    @Override
    public void setLocationURI(String value) {
        this.locationURI = value;
    }

    @Override
    public String getImportType() {
        return importType;
    }

    @Override
    public void setImportType(String value) {
        this.importType = value;
    }

}
