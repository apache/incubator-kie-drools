package org.kie.dmn.model.v1_2;

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
