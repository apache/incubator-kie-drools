package org.kie.pmml.commons.model.expressions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class KiePMMLInlineTableTest {

    private static final List<KiePMMLRow> ROWS;

    static {
        ROWS = IntStream.range(0, 4)
                .mapToObj(i -> {
                    Map<String, Object> columnValues = IntStream.range(0, 3)
                            .boxed()
                            .collect(Collectors.toMap(j -> "KEY-" + i + "-" + j,
                                                      j -> "VALUE-" + i + "-" + j));
                    return new KiePMMLRow(columnValues);
                })
                .collect(Collectors.toList());
    }

    @Test
    void evaluateKeyNotFound() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("NOT-KEY", 0), "KEY-0-0",
                                                                 null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundNotMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("KEY-1-1", 435345), "KEY-0" +
                "-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(Collections.singletonMap("KEY-1-1", "VALUE-1-1"),
                                                                 "KEY-1-2", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo("VALUE-1-2");
    }

    @Test
    void evaluateKeyFoundMultipleNotMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Map<String, Object> columnPairsMap = IntStream.range(0, 2).boxed()
                .collect(Collectors.toMap(i -> "KEY-1-" + i,
                                          i -> "VALUE-1-" + i));
        columnPairsMap.put("KEY-1-2", 4);
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(columnPairsMap, "KEY-0-0", null);
        assertThat(retrieved).isNotPresent();
    }

    @Test
    void evaluateKeyFoundMultipleMatching() {
        KiePMMLInlineTable kiePMMLInlineTable = new KiePMMLInlineTable("name", Collections.emptyList(), ROWS);
        Map<String, Object> columnPairsMap = IntStream.range(0, 3).boxed()
                .collect(Collectors.toMap(i -> "KEY-1-" + i,
                                          i -> "VALUE-1-" + i));
        Optional<Object> retrieved = kiePMMLInlineTable.evaluate(columnPairsMap, "KEY-1-2", null);
        assertThat(retrieved).isPresent();
        assertThat(retrieved.get()).isEqualTo("VALUE-1-2");
    }
}