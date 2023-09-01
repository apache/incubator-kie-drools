package org.kie.efesto.common.api.identifiers.componentroots;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalComponentIdA extends LocalUriId implements Id {

    public static final String PREFIX = "testingcomponentA";

    private final String fileName;
    private final String name;

    public LocalComponentIdA(String fileName, String name) {
        super(LocalUri.Root.append(PREFIX).append(fileName).append(name));
        this.fileName = fileName;
        this.name = name;
    }

    public String getFileName() {
        return fileName;
    }

    public String name() {
        return name;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

}
