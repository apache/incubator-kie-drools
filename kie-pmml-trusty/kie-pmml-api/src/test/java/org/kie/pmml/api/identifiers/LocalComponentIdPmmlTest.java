package org.kie.pmml.api.identifiers;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.LocalId;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.LocalUriId;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class LocalComponentIdPmmlTest {

    private static final String fileName = "fileName";
    private static final String name = "name";

    @Test
    void equals() {
        LocalComponentIdPmml original = new LocalComponentIdPmml(fileName, name);
        LocalUriId compare = new LocalComponentIdPmml(fileName, name);
        assertThat(original.equals(compare)).isTrue();
        String path = original.fullPath();
        LocalUri parsed = LocalUri.parse(path);
        compare = new ModelLocalUriId(parsed);
        assertThat(original.equals(compare)).isTrue();
    }

    @Test
    void prefix() {
        String retrieved = new LocalComponentIdPmml(fileName, name).asLocalUri().toUri().getPath();
        String expected = SLASH + LocalComponentIdPmml.PREFIX + SLASH;
        assertThat(retrieved).startsWith(expected);
    }

    @Test
    void getFileName() {
        LocalComponentIdPmml retrieved = new LocalComponentIdPmml(fileName, name);
        assertThat(retrieved.getFileName()).isEqualTo(fileName);
    }

    @Test
    void name() {
        LocalComponentIdPmml retrieved = new LocalComponentIdPmml(fileName, name);
        assertThat(retrieved.name()).isEqualTo(name);
    }

    @Test
    void toLocalId() {
        LocalComponentIdPmml LocalComponentIdPmml = new LocalComponentIdPmml(fileName, name);
        LocalId retrieved = LocalComponentIdPmml.toLocalId();
        assertThat(retrieved).isEqualTo(LocalComponentIdPmml);
    }
}