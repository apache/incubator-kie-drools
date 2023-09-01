package org.drools.verifier.core.index.select;

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ExactMatcherSearchTest {

    private ExactMatcherSearch<Object> search;

    private MultiMap<Value, Object, List<Object>> map = MultiMapFactory.make();

    @BeforeEach
    public void setUp() throws Exception {
        map.put(new Value(null), "I am null");
        map.put(new Value("helloKey"), "hello");

    }

    @Test
    void testNullSearch() throws Exception {
        search = new ExactMatcherSearch<>(new ExactMatcher(KeyDefinition.newKeyDefinition().withId("value").build(),
                        null),
                map);
        MultiMap<Value, Object, List<Object>> search1 = search.search();
        
        assertThat(search1.get(new Value(null)).get(0)).isEqualTo("I am null");

    }

    @Test
    void testNegatedNullSearch() throws Exception {
        search = new ExactMatcherSearch<>(new ExactMatcher(KeyDefinition.newKeyDefinition().withId("value").build(),
                        null,
                        true),
                map);
        MultiMap<Value, Object, List<Object>> search1 = search.search();
        
        assertThat(search1.get(new Value("helloKey")).get(0)).isEqualTo("hello");

    }
}