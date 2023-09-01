package org.drools.verifier.core.index.select;

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectKeyMatcherTest {

    private Select<String> select;

    @BeforeEach
    public void setUp() throws Exception {
        final MultiMap<Value, String, List<String>> map = MultiMapFactory.make();
        map.put(new Value("value1"),
                "value1");
        map.put(new Value("value2"),
                "value2");

        select = new Select<>(map,
                              new Matcher(KeyDefinition.newKeyDefinition().withId("name").build()));
    }

    @Test
    void testAll() throws Exception {
        assertThat(select.all()).hasSize(2);
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isEqualTo("value1");
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isEqualTo("value2");
    }
}