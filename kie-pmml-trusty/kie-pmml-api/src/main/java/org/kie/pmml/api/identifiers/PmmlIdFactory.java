package org.kie.pmml.api.identifiers;

public class PmmlIdFactory implements PmmlComponentRoot {

    public LocalComponentIdPmml get(String fileName, String name) {
        return new LocalComponentIdPmml(fileName, name);
    }

}
