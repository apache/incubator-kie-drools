package org.kie.drl.api.identifiers;

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.Id;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

public class LocalComponentIdDrlSession extends ModelLocalUriId implements Id {

    public static final String PREFIX = "drl";
    private final long identifier;

    public LocalComponentIdDrlSession(String basePath, long identifier) {
        super(appendBasePath(LocalUri.Root.append(PREFIX), basePath).append(String.valueOf(identifier)));
        this.identifier = identifier;
    }

    public long identifier() {
        return identifier;
    }

    @Override
    public LocalId toLocalId() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        if (!super.equals(o)) {
            return false;
        }
        LocalComponentIdDrlSession that = (LocalComponentIdDrlSession) o;
        return identifier == that.identifier;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), identifier);
    }
}
