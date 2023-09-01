package org.kie.drl.api.identifiers;

public class DrlSessionIdFactory implements DrlComponentRoot {

    public LocalComponentIdDrlSession get(String basePath, long identifier) {
        return new LocalComponentIdDrlSession(basePath, identifier);
    }
}
