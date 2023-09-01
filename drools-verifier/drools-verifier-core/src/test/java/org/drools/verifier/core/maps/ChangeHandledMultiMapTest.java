package org.drools.verifier.core.maps;

import java.util.List;

import org.drools.verifier.core.index.keys.Value;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ChangeHandledMultiMapTest {

    private MultiMap<Value, String, List<String>> map;
    private MultiMapChangeHandler.ChangeSet<Value, String> changeSet;

    private int timesCalled = 0;

    @BeforeEach
    public void setUp() throws Exception {
        this.timesCalled = 0;

        this.map = MultiMapFactory.make(true);
        this.map.addChangeListener(new MultiMapChangeHandler<Value, String>() {
            @Override
            public void onChange(final ChangeSet<Value, String> changeSet) {
                ChangeHandledMultiMapTest.this.changeSet = changeSet;
                timesCalled++;
            }
        });
    }

    @Test
    void testSize() throws Exception {
        assertThat(changeSet).isNull();
        assertThat(timesCalled).isEqualTo(0);
    }

    @Test
    void testPut() throws Exception {
        map.put(new Value("hello"), "test");

        assertThat(changeSet.getAdded().get(new Value("hello"))).contains("test");
        assertThat(timesCalled).isEqualTo(1);
    }

    @Test
    void testAddAllValues() throws Exception {
        final List<String> list = List.of("a", "b", "c");

        map.addAllValues(new Value("hello"), list);

        assertThat(changeSet.getAdded().get(new Value("hello"))).hasSize(3).contains("a", "b", "c");
        assertThat(timesCalled).isEqualTo(1);
    }
}