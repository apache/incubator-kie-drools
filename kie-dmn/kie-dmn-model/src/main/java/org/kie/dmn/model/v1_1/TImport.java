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
package org.kie.dmn.model.v1_1;

import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Import;

public class TImport extends KieDMNModelInstrumentedBase implements Import, NotADMNElementInV11 {

    public static final QName NAME_QNAME = new QName(KieDMNModelInstrumentedBase.URI_KIE, "name");
    public static final QName MODELNAME_QNAME = new QName(KieDMNModelInstrumentedBase.URI_KIE, "modelName");

    private String namespace;
    private String locationURI;
    private String importType;

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace( final String value ) {
        this.namespace = value;
    }

    @Override
    public String getLocationURI() {
        return locationURI;
    }

    @Override
    public void setLocationURI( final String value ) {
        this.locationURI = value;
    }

    @Override
    public String getImportType() {
        return importType;
    }

    @Override
    public void setImportType( final String value ) {
        this.importType = value;
    }

    /**
     * @since DMN v1.2
     */
    @Override
    public String getName() {
        return getAdditionalAttributes().get(NAME_QNAME);
    }

    /**
     * @since DMN v1.2
     */
    @Override
    public void setName(String value) {
        getAdditionalAttributes().put(NAME_QNAME, value);
    }

}
