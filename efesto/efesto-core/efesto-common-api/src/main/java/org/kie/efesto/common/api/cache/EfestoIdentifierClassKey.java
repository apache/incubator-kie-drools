package org.kie.efesto.common.api.cache;

import java.util.Objects;

import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

/**
 * Key used by efesto second-level cache based on <code>EfestoClassKey</code> and
 * <code>ModelLocalUriId</code>
 */
public class EfestoIdentifierClassKey {

    private final ModelLocalUriId modelLocalUriId;
    private final EfestoClassKey efestoClassKey;

    public EfestoIdentifierClassKey(ModelLocalUriId modelLocalUriId, EfestoClassKey efestoClassKey) {
        this.modelLocalUriId = modelLocalUriId;
        this.efestoClassKey = efestoClassKey;
    }

    @Override
    public String toString() {
        return "EfestoIdentifierClassKey{" +
                "modelLocalUriId=" + modelLocalUriId +
                ", efestoClassKey=" + efestoClassKey +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        EfestoIdentifierClassKey that = (EfestoIdentifierClassKey) o;
        return Objects.equals(modelLocalUriId, that.modelLocalUriId) && Objects.equals(efestoClassKey,
                                                                                       that.efestoClassKey);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modelLocalUriId, efestoClassKey);
    }
}
