package org.drools.verifier.core.index.select;

import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectExactMatcherTest {

    private Select<Item> select;
    private MultiMap<Value, Item, List<Item>> map;
    private Item thirteen;

    @BeforeEach
    public void setUp() throws Exception {
        map = MultiMapFactory.make();
        thirteen = new Item(13);

        map.put(new Value(0), new Item(0));
        map.put(new Value(13), thirteen);
        map.put(new Value(56), new Item(56));
        map.put(new Value(100), new Item(100));
        map.put(new Value(1200), new Item(1200));

        select = new Select<>(map,
                               new ExactMatcher(KeyDefinition.newKeyDefinition().withId("cost").build(),
                                                 13));
    }

    @Test
    void testAll() throws Exception {
        final Collection<Item> all = select.all();

        assertThat(all).hasSize(1);
    }

    @Test
    void testFirst() throws Exception {
        assertThat(select.first()).isEqualTo(thirteen);
    }

    @Test
    void testLast() throws Exception {
        assertThat(select.last()).isEqualTo(thirteen);
    }

}