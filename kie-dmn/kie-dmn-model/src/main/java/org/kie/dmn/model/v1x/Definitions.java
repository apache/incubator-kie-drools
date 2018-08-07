package org.kie.dmn.model.v1x;

import java.util.List;

import org.kie.dmn.model.v1x.dmndi.DMNDI;

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
