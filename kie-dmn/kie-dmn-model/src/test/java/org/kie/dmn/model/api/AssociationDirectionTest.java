package org.kie.dmn.model.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class AssociationDirectionTest {

    @Test
    public void testFromValue() {
        assertThat(AssociationDirection.fromValue("None")).isEqualTo(AssociationDirection.NONE);
        assertThat(AssociationDirection.fromValue("One")).isEqualTo(AssociationDirection.ONE);
        assertThat(AssociationDirection.fromValue("Both")).isEqualTo(AssociationDirection.BOTH);
        assertThatIllegalArgumentException().isThrownBy(() -> AssociationDirection.fromValue("asd"));
    }

}
