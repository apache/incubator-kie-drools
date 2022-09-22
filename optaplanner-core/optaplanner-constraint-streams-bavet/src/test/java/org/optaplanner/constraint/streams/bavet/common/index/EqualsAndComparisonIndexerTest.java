package org.optaplanner.constraint.streams.bavet.common.index;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.optaplanner.constraint.streams.bavet.common.collection.TupleListEntry;
import org.optaplanner.constraint.streams.bavet.uni.UniTuple;
import org.optaplanner.constraint.streams.bavet.uni.UniTupleImpl;
import org.optaplanner.constraint.streams.common.bi.DefaultBiJoiner;
import org.optaplanner.core.api.score.stream.Joiners;

class EqualsAndComparisonIndexerTest extends AbstractIndexerTest {

    private final DefaultBiJoiner<Person, Person> joiner =
            (DefaultBiJoiner<Person, Person>) Joiners.equal((Person p) -> p.gender)
                    .and(Joiners.lessThanOrEqual(a -> a.age));

    @Test
    void iEmpty() {
        Indexer<UniTuple<String>> indexer = new IndexerFactory(joiner).buildIndexer(true);
        assertThat(getTuples(indexer, "F", 40)).isEmpty();
    }

    @Test
    void put() {
        Indexer<UniTuple<String>> indexer = new IndexerFactory(joiner).buildIndexer(true);
        UniTuple<String> annTuple = newTuple("Ann-F-40");
        assertThat(indexer.size(new ManyIndexProperties("F", 40))).isEqualTo(0);
        indexer.put(new ManyIndexProperties("F", 40), annTuple);
        assertThat(indexer.size(new ManyIndexProperties("F", 40))).isEqualTo(1);
    }

    @Test
    void removeTwice() {
        Indexer<UniTuple<String>> indexer = new IndexerFactory(joiner).buildIndexer(true);
        UniTuple<String> annTuple = newTuple("Ann-F-40");
        TupleListEntry<UniTuple<String>> annEntry = indexer.put(new ManyIndexProperties("F", 40), annTuple);

        indexer.remove(new ManyIndexProperties("F", 40), annEntry);
        assertThatThrownBy(() -> indexer.remove(new ManyIndexProperties("F", 40), annEntry))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void visit() {
        Indexer<UniTuple<String>> indexer = new IndexerFactory(joiner).buildIndexer(true);

        UniTuple<String> annTuple = newTuple("Ann-F-40");
        indexer.put(new ManyIndexProperties("F", 40), annTuple);
        UniTuple<String> bethTuple = newTuple("Beth-F-30");
        indexer.put(new ManyIndexProperties("F", 30), bethTuple);
        indexer.put(new ManyIndexProperties("M", 40), newTuple("Carl-M-40"));
        indexer.put(new ManyIndexProperties("M", 30), newTuple("Dan-M-30"));
        UniTuple<String> ednaTuple = newTuple("Edna-F-40");
        indexer.put(new ManyIndexProperties("F", 40), ednaTuple);

        assertThat(getTuples(indexer, "F", 40)).containsOnly(annTuple, bethTuple, ednaTuple);
        assertThat(getTuples(indexer, "F", 35)).containsOnly(bethTuple);
        assertThat(getTuples(indexer, "F", 30)).containsOnly(bethTuple);
        assertThat(getTuples(indexer, "F", 20)).isEmpty();
    }

    private static UniTuple<String> newTuple(String factA) {
        return new UniTupleImpl<>(factA, 0);
    }

}
