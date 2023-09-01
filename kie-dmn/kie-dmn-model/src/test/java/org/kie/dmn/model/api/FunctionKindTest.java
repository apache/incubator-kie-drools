package org.kie.dmn.model.api;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

public class FunctionKindTest {

    @Test
    public void testFromValue() {
        assertThat(FunctionKind.fromValue("FEEL")).isEqualTo(FunctionKind.FEEL);
        assertThat(FunctionKind.fromValue("Java")).isEqualTo(FunctionKind.JAVA);
        assertThat(FunctionKind.fromValue("JAVA")).isEqualTo(FunctionKind.JAVA);
        assertThat(FunctionKind.fromValue("PMML")).isEqualTo(FunctionKind.PMML);
        assertThatIllegalArgumentException().isThrownBy(() -> FunctionKind.fromValue("asd"));
    }

}
