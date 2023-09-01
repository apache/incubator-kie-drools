package org.kie.pmml.api.identifiers;

public class PmmlIdRedirectFactory implements PmmlComponentRoot {

    public LocalComponentIdRedirectPmml get(String redirectModel, String fileName, String name) {
        return new LocalComponentIdRedirectPmml(redirectModel, fileName, name);
    }
}
