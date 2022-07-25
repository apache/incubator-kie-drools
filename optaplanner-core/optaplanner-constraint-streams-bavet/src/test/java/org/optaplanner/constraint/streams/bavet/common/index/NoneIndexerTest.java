package org.optaplanner.constraint.streams.bavet.common.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.SoftAssertions.assertSoftly;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTupleImpl;

class NoneIndexerTest extends AbstractIndexerTest {

    @Test
    void getEmpty() {
        Indexer<UniTuple<String>, String> indexer = new NoneIndexer<>();
        assertSoftly(softly -> {
            softly.assertThat(getTupleMap(indexer)).isEmpty();
            softly.assertThat(indexer.isEmpty()).isTrue();
        });
    }

    @Test
    void putTwice() {
        Indexer<UniTuple<String>, String> indexer = new NoneIndexer<>();
        UniTuple<String> annTuple = newTuple("Ann-F-40");
        indexer.put(NoneIndexProperties.INSTANCE, annTuple, "Ann value");
        assertSoftly(softly -> {
            softly.assertThat(indexer.isEmpty()).isFalse();
            softly.assertThat(getTupleMap(indexer)).containsExactly(Map.entry(annTuple, "Ann value"));
        });
        assertThatThrownBy(() -> indexer.put(NoneIndexProperties.INSTANCE, annTuple, "Ann value"))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void removeTwice() {
        Indexer<UniTuple<String>, String> indexer = new NoneIndexer<>();
        UniTuple<String> annTuple = newTuple("Ann-F-40");
        indexer.put(NoneIndexProperties.INSTANCE, annTuple, "Ann value");
        assertSoftly(softly -> {
            softly.assertThat(indexer.isEmpty()).isFalse();
            softly.assertThat(getTupleMap(indexer)).containsExactly(Map.entry(annTuple, "Ann value"));
        });

        UniTuple<String> ednaTuple = newTuple("Edna-F-40");
        assertThatThrownBy(() -> indexer.remove(NoneIndexProperties.INSTANCE, ednaTuple))
                .isInstanceOf(IllegalStateException.class);
        assertThat(indexer.remove(NoneIndexProperties.INSTANCE, annTuple))
                .isEqualTo("Ann value");
        assertSoftly(softly -> {
            softly.assertThat(indexer.isEmpty()).isTrue();
            softly.assertThat(getTupleMap(indexer)).isEmpty();
        });
        assertThatThrownBy(() -> indexer.remove(NoneIndexProperties.INSTANCE, annTuple))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void visit() {
        Indexer<UniTuple<String>, String> indexer = new NoneIndexer<>();

        UniTuple<String> annTuple = newTuple("Ann-F-40");
        indexer.put(NoneIndexProperties.INSTANCE, annTuple, "Ann value");
        UniTuple<String> bethTuple = newTuple("Beth-F-30");
        indexer.put(NoneIndexProperties.INSTANCE, bethTuple, "Beth value");

        assertThat(getTupleMap(indexer)).containsOnlyKeys(annTuple, bethTuple);
    }

    private static UniTuple<String> newTuple(String factA) {
        return new UniTupleImpl<>(factA, 0);
    }

}
