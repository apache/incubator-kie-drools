package org.kie.drl.api.identifiers;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.identifiers.EfestoAppRoot;
import org.kie.efesto.common.api.identifiers.LocalUri;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.efesto.common.api.identifiers.LocalUri.SLASH;

class DrlSessionIdFactoryTest {

    @Test
    void get() {
        long identifier = Math.abs(new Random().nextLong());
        String basePath = "/TestingRule/TestedRule";
        ModelLocalUriId modelLocalUriId = new ModelLocalUriId(LocalUri.parse("/pmml" + basePath));
        assertThat(modelLocalUriId.model()).isEqualTo("pmml");
        assertThat(modelLocalUriId.basePath()).isEqualTo(basePath);
        LocalComponentIdDrlSession retrieved = new EfestoAppRoot()
                .get(KieDrlComponentRoot.class)
                .get(DrlSessionIdFactory.class)
                .get(modelLocalUriId.basePath(), identifier);
        assertThat(retrieved.model()).isEqualTo(LocalComponentIdDrlSession.PREFIX);
        String expected = basePath + SLASH + identifier;
        assertThat(retrieved.basePath()).isEqualTo(expected);
    }
}