package org.kie.drl.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class LocalComponentIdDrlSessionTest {

    private static final String basePath = "basePath";
    private static final long identifier = 23423432L;

    @Test
    void prefix() {
        String retrieved = new LocalComponentIdDrlSession(basePath, identifier).asLocalUri().toUri().getPath();
        String expected = SLASH + LocalComponentIdDrlSession.PREFIX + SLASH;
        assertThat(retrieved).startsWith(expected);
    }

    @Test
    void identifier() {
        LocalComponentIdDrlSession retrieved = new LocalComponentIdDrlSession(basePath, identifier);
        assertThat(retrieved.identifier()).isEqualTo(identifier);
    }

    @Test
    void toLocalId() {
        LocalComponentIdDrlSession LocalComponentIdDrlSession = new LocalComponentIdDrlSession(basePath, identifier);
        LocalId retrieved = LocalComponentIdDrlSession.toLocalId();
        assertThat(retrieved).isEqualTo(LocalComponentIdDrlSession);
    }
}