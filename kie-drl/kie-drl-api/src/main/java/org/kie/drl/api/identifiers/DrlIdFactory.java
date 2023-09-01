package org.kie.drl.api.identifiers;

public class DrlIdFactory implements DrlComponentRoot {

    public LocalComponentIdDrl get(String basePath) {
        return new LocalComponentIdDrl(basePath);
    }
}
