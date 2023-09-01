package org.kie.dmn.model.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class HitPolicyTest {

    @Test
    public void testFromValue() {
        assertThatIllegalArgumentException().isThrownBy(() -> HitPolicy.fromValue("asd"));
    }

    @Test
    public void testMultiHit() {
        assertThat(HitPolicy.UNIQUE.isMultiHit()).isFalse();
        assertThat(HitPolicy.FIRST.isMultiHit()).isFalse();
        assertThat(HitPolicy.PRIORITY.isMultiHit()).isFalse();
        assertThat(HitPolicy.ANY.isMultiHit()).isFalse();
        assertThat(HitPolicy.COLLECT.isMultiHit()).isTrue();
        assertThat(HitPolicy.RULE_ORDER.isMultiHit()).isTrue();
        assertThat(HitPolicy.OUTPUT_ORDER.isMultiHit()).isTrue();
    }

}
