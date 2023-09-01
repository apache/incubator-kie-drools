package org.kie.pmml.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;

public class LocalPredictionId extends LocalUriId implements Id {
    public static final String PREFIX = "predictions";

    private final String fileName;
    private final String name;

    public LocalPredictionId(String fileName, String name) {
        super(LocalUri.Root.append(PREFIX).append(name));
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
