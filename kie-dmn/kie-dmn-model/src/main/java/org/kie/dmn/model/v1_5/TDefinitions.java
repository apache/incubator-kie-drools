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
package org.kie.dmn.model.v1_5;

import org.kie.dmn.model.api.Artifact;
import org.kie.dmn.model.api.BusinessContextElement;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.ElementCollection;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.dmndi.DMNDI;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TDefinitions extends TNamedElement implements Definitions {

    public static final String DEFAULT_EXPRESSION_LANGUAGE = URI_FEEL;

    public static final String DEFAULT_TYPE_LANGUAGE = URI_FEEL;

    protected List<Import> _import;
    protected List<ItemDefinition> itemDefinition;
    protected List<DRGElement> drgElement;
    protected List<Artifact> artifact;
    protected List<ElementCollection> elementCollection;
    protected List<BusinessContextElement> businessContextElement;
    protected DMNDI dmndi;
    protected String expressionLanguage;
    protected String typeLanguage;
    protected String namespace;
    protected String exporter;
    protected String exporterVersion;

    @Override
    public List<Import> getImport() {
        if (_import == null) {
            _import = new ArrayList<>();
        }
        return this._import;
    }

    @Override
    public List<ItemDefinition> getItemDefinition() {
        if (itemDefinition == null) {
            itemDefinition = new ArrayList<>();
        }
        return this.itemDefinition;
    }

    @Override
    public List<DRGElement> getDrgElement() {
        if (drgElement == null) {
            drgElement = new ArrayList<>();
        }
        return this.drgElement;
    }

    @Override
    public List<Artifact> getArtifact() {
        if (artifact == null) {
            artifact = new ArrayList<>();
        }
        return this.artifact;
    }

    @Override
    public List<ElementCollection> getElementCollection() {
        if (elementCollection == null) {
            elementCollection = new ArrayList<>();
        }
        return this.elementCollection;
    }

    @Override
    public List<BusinessContextElement> getBusinessContextElement() {
        if (businessContextElement == null) {
            businessContextElement = new ArrayList<>();
        }
        return this.businessContextElement;
    }

    @Override
    public DMNDI getDMNDI() {
        return dmndi;
    }

    @Override
    public void setDMNDI(DMNDI value) {
        this.dmndi = value;
    }

    @Override
    public String getExpressionLanguage() {
        if (expressionLanguage == null) {
            return DEFAULT_EXPRESSION_LANGUAGE;
        } else {
            return expressionLanguage;
        }
    }

    @Override
    public String getTypeLanguage() {
        if (typeLanguage == null) {
            return DEFAULT_TYPE_LANGUAGE;
        } else {
            return typeLanguage;
        }
    }

    @Override
    public void setExpressionLanguage(String value) {
        this.expressionLanguage = value;
    }

    @Override
    public void setTypeLanguage(String value) {
        this.typeLanguage = value;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace(String value) {
        this.namespace = value;
    }

    @Override
    public String getExporter() {
        return exporter;
    }

    @Override
    public void setExporter(String value) {
        this.exporter = value;
    }

    @Override
    public String getExporterVersion() {
        return exporterVersion;
    }

    @Override
    public void setExporterVersion(String value) {
        this.exporterVersion = value;
    }

    /**
     * Implementing support for internal model
     */
    @Override
    public List<DecisionService> getDecisionService() {
        return drgElement.stream().filter(DecisionService.class::isInstance).map(DecisionService.class::cast).collect(Collectors.toList());
    }

    public void normalize() {
        for (ItemDefinition itemDefinition : this.getItemDefinition()) {
            processQNameURIs(itemDefinition);
        }
    }

    private static void processQNameURIs(ItemDefinition iDef) {
        final QName typeRef = iDef.getTypeRef();
        if (typeRef != null && XMLConstants.NULL_NS_URI.equals(typeRef.getNamespaceURI())) {
            final String namespace = iDef.getNamespaceURI(typeRef.getPrefix());
            iDef.setTypeRef(new QName(namespace, typeRef.getLocalPart(), typeRef.getPrefix()));
        }
        for (ItemDefinition comp : iDef.getItemComponent()) {
            processQNameURIs(comp);
        }
    }
}
