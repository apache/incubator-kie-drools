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
package org.kie.dmn.model.api;

import java.util.List;

import javax.xml.XMLConstants;

import org.kie.dmn.model.api.dmndi.DMNDI;

public interface Definitions extends NamedElement {

    List<Import> getImport();

    List<ItemDefinition> getItemDefinition();

    List<DRGElement> getDrgElement();

    List<Artifact> getArtifact();

    List<ElementCollection> getElementCollection();

    List<BusinessContextElement> getBusinessContextElement();

    /**
     * Internal model: this will filter from DRGElement the Decision Service
     */
    List<DecisionService> getDecisionService();

    /**
     * Internal model: mutates the current Definitions' ItemDefinition(s) typeRef QName to be properly valorized in the namespaces.
     * 
     * Utility method to ensure any QName references contained inside the ItemDefinitions have the namespace correctly valorized, also accordingly to the prefix.
     * (Even in the case of {@link XMLConstants.DEFAULT_NS_PREFIX} it will take the DMN model namespace for the no-prefix accordingly.)
     */
    void normalize();

    String getExpressionLanguage();

    void setExpressionLanguage(String value);

    String getTypeLanguage();

    void setTypeLanguage(String value);

    String getNamespace();

    void setNamespace(String value);

    String getExporter();

    void setExporter(String value);

    String getExporterVersion();

    void setExporterVersion(String value);

    /**
     * @since DMN v1.2
     */
    DMNDI getDMNDI();

    /**
     * @since DMN v1.2
     */
    void setDMNDI(DMNDI value);

}
