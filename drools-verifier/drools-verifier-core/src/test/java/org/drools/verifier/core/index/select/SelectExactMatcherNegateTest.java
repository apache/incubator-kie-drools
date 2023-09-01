package org.drools.verifier.core.index.select;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.KeyDefinition;
import org.drools.verifier.core.maps.MultiMap;
import org.drools.verifier.core.maps.MultiMapFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import static org.assertj.core.api.Assertions.assertThat;

public class SelectExactMatcherNegateTest {

    private Select<Item> select;

    private MultiMap<Value, Item, List<Item>> makeMap() {
        final MultiMap<Value, Item, List<Item>> itemKeyTreeMap = MultiMapFactory.make();

        itemKeyTreeMap.put(new Value(null), new Item(null));
        itemKeyTreeMap.put(new Value(0), new Item(0));
        itemKeyTreeMap.put(new Value(56), new Item(56));
        itemKeyTreeMap.put(new Value(100), new Item(100));
        itemKeyTreeMap.put(new Value(1200), new Item(1200));
        return itemKeyTreeMap;
    }

    public static Collection<Object[]> testData() {
        return Arrays.asList(new Object[][]{
                {5, null, 1200, new ExactMatcher(KeyDefinition.newKeyDefinition().withId("cost").build(),
                        13,
                        true)},
                {4, 0, 1200, new ExactMatcher(KeyDefinition.newKeyDefinition().withId("cost").build(),
                        null,
                        true)},
        });
    }


    @MethodSource("testData")
    @ParameterizedTest
    void testAll(final int amount, final Object firstValue, final Object lastValue, final Matcher matcher) throws Exception {
        this.select = new Select<>(makeMap(), matcher);
        
        assertThat(select.all()).hasSize(amount);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testFirst(final int amount, final Object firstValue, final Object lastValue, final Matcher matcher) throws Exception {
        this.select = new Select<>(makeMap(), matcher);
        
        assertThat(select.first().cost).isEqualTo(firstValue);
    }

    @MethodSource("testData")
    @ParameterizedTest
    void testLast(final int amount, final Object firstValue, final Object lastValue, final Matcher matcher) throws Exception {
        this.select = new Select<>(makeMap(), matcher);
        
        assertThat(select.last().cost).isEqualTo(lastValue);
    }
}