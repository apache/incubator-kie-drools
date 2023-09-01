package org.kie.drl.api.identifiers;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class LocalComponentIdDrl extends ModelLocalUriId implements Id {

    public static final String PREFIX = "drl";

    public LocalComponentIdDrl(String basePath) {
        super(appendBasePath(LocalUri.Root.append(PREFIX),basePath));
    }

    @Override
    public LocalId toLocalId() {
        return this;
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
