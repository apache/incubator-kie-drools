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
