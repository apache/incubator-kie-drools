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
