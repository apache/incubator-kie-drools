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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;

import org.kie.dmn.model.api.Artifact;
import org.kie.dmn.model.api.BusinessContextElement;
import org.kie.dmn.model.api.DRGElement;
import org.kie.dmn.model.api.DecisionService;
import org.kie.dmn.model.api.Definitions;
import org.kie.dmn.model.api.ElementCollection;
import org.kie.dmn.model.api.Import;
import org.kie.dmn.model.api.ItemDefinition;
import org.kie.dmn.model.api.dmndi.DMNDI;
import org.kie.dmn.model.v1_1.extensions.DecisionServices;

public class TDefinitions extends TNamedElement implements Definitions {

    public static final String DEFAULT_EXPRESSION_LANGUAGE = "http://www.omg.org/spec/FEEL/20140401";

    public static final String DEFAULT_TYPE_LANGUAGE = "http://www.omg.org/spec/FEEL/20140401";

    private List<Import> _import;
    private List<ItemDefinition> itemDefinition;
    private List<DRGElement> drgElement;
    private List<Artifact> artifact;
    private List<ElementCollection> elementCollection;
    private List<BusinessContextElement> businessContextElement;
    private List<DecisionService> decisionService;
    private String expressionLanguage;
    private String typeLanguage;
    private String namespace;
    private String exporter;
    private String exporterVersion;

    @Override
    public List<Import> getImport() {
        if ( _import == null ) {
            _import = new ArrayList<>();
        }
        return this._import;
    }

    @Override
    public List<ItemDefinition> getItemDefinition() {
        if ( itemDefinition == null ) {
            itemDefinition = new ArrayList<>();
        }
        return this.itemDefinition;
    }

    @Override
    public List<DRGElement> getDrgElement() {
        if ( drgElement == null ) {
            drgElement = new ArrayList<>();
        }
        return this.drgElement;
    }

    @Override
    public List<Artifact> getArtifact() {
        if ( artifact == null ) {
            artifact = new ArrayList<>();
        }
        return this.artifact;
    }

    @Override
    public List<ElementCollection> getElementCollection() {
        if ( elementCollection == null ) {
            elementCollection = new ArrayList<>();
        }
        return this.elementCollection;
    }

    @Override
    public List<BusinessContextElement> getBusinessContextElement() {
        if ( businessContextElement == null ) {
            businessContextElement = new ArrayList<>();
        }
        return this.businessContextElement;
    }

    @Override
    public List<DecisionService> getDecisionService() {
        if ( decisionService == null ) {
            decisionService = new ArrayList<>();
            // as DMN1.1 xsd is broken to allow proper persistence of DecisionService, do fetch them from extensions.
			if ( getExtensionElements() != null ) {
                List<DecisionService> collectDS = getExtensionElements().getAny().stream()
                                                                    .filter(DecisionServices.class::isInstance).map(DecisionServices.class::cast)
                                                                    .flatMap(dss -> dss.getDecisionService().stream())
                                                                    .collect(Collectors.toList());
				decisionService.addAll(collectDS);
			}
        }
        return this.decisionService;
    }


    @Override
    public String getExpressionLanguage() {
        if ( expressionLanguage == null ) {
            return DEFAULT_EXPRESSION_LANGUAGE;
        } else {
            return expressionLanguage;
        }
    }

    @Override
    public void setExpressionLanguage( final String value ) {
        this.expressionLanguage = value;
    }

    @Override
    public String getTypeLanguage() {
        if ( typeLanguage == null ) {
            return DEFAULT_TYPE_LANGUAGE;
        } else {
            return typeLanguage;
        }
    }

    @Override
    public void setTypeLanguage( final String value ) {
        this.typeLanguage = value;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public void setNamespace( final String value ) {
        this.namespace = value;
    }

    @Override
    public String getExporter() {
        return exporter;
    }

    @Override
    public void setExporter( final String value ) {
        this.exporter = value;
    }

    @Override
    public String getExporterVersion() {
        return exporterVersion;
    }

    @Override
    public void setExporterVersion( final String value ) {
        this.exporterVersion = value;
    }

    @Override
    public String toString() {
        return "Definitions{" +
               "name=" + getName() +
               ", _import=" + _import +
               ", itemDefinition=" + itemDefinition +
               ", drgElement=" + drgElement +
               ", decisionService=" + decisionService +
               ", artifact=" + artifact +
               ", elementCollection=" + elementCollection +
               ", businessContextElement=" + businessContextElement +
               ", expressionLanguage='" + expressionLanguage + '\'' +
               ", typeLanguage='" + typeLanguage + '\'' +
               ", namespace='" + namespace + '\'' +
               ", exporter='" + exporter + '\'' +
               ", exporterVersion='" + exporterVersion + '\'' +
               '}';
    }

    /**
     * Utility method to ensure any QName references contained inside the ItemDefinitions have the namespace correctly valorized, also accordingly to the prefix.
     * (Even in the case of {@link XMLConstants.DEFAULT_NS_PREFIX} it will take the DMN model namespace for the no-prefix accordingly.)
     */
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

    @Override
    public DMNDI getDMNDI() {
        throw new UnsupportedOperationException("not on 1.1");
    }

    @Override
    public void setDMNDI(DMNDI value) {
        throw new UnsupportedOperationException("not on 1.1");
    }
}
