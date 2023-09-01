package org.drools.model.codegen.execmodel.generator;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.drools.modelcompiler.util.StringUtil.toId;

public class StringUtilTest {

    @Test
    public void test() {
        assertThat(toId("123stella")).isEqualTo("__123stella");
        assertThat(toId("123_stella")).isEqualTo("__123__stella");
        assertThat(toId("_stella")).isEqualTo("__stella");
        assertThat(toId("_stella_123")).isEqualTo("__stella__123");
        assertThat(toId("my stella")).isEqualTo("my_32stella");
        assertThat(toId("$tella")).isEqualTo("$tella");
        assertThat(toId("$tella(123)")).isEqualTo("$tella_40123_41");
        assertThat(toId("my-stella")).isEqualTo("my_45stella");
        assertThat(toId("my+stella")).isEqualTo("my_43stella");
        assertThat(toId("o'stella")).isEqualTo("o_39stella");
        assertThat(toId("stella&you")).isEqualTo("stella_38you");
        assertThat(toId("stella & Co.")).isEqualTo("stella_32_38_32Co_46");
    }
}
