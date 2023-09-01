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
