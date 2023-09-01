package org.kie.efesto.runtimemanager.api.model;

import org.junit.jupiter.api.Test;
import org.kie.efesto.common.api.cache.EfestoClassKey;
import org.kie.efesto.common.api.identifiers.ModelLocalUriId;

import static org.assertj.core.api.Assertions.assertThat;

class EfestoInputTest {

    @Test
    void getFirstLevelCacheKeySameParentClass() {
        EfestoInput input = new BaseEfestoInput(null, "ONE");
        EfestoClassKey expected = new EfestoClassKey(BaseEfestoInput.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isEqualTo(expected);
    }

    @Test
    void getFirstLevelCacheKeyDifferentParentClass() {
        EfestoInput input = new BaseEfestoInputExtender(null, "ONE");
        EfestoClassKey unexpected = new EfestoClassKey(BaseEfestoInput.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isNotEqualTo(unexpected);
    }

    @Test
    void getFirstLevelCacheKeySameChildClass() {
        EfestoInput input = new BaseEfestoInputExtender(null, "ONE");
        EfestoClassKey expected = new EfestoClassKey(BaseEfestoInputExtender.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isEqualTo(expected);
    }

    @Test
    void getFirstLevelCacheKeyDifferentChildClass() {
        EfestoInput input = new BaseEfestoInput(null, "ONE");
        EfestoClassKey unexpected = new EfestoClassKey(BaseEfestoInputExtender.class, String.class);
        assertThat(input.getFirstLevelCacheKey()).isNotEqualTo(unexpected);
    }

    static class BaseEfestoInputExtender extends BaseEfestoInput<String> {

        public BaseEfestoInputExtender(ModelLocalUriId modelLocalUriId, String inputData) {
            super(modelLocalUriId, inputData);
        }
    }
}