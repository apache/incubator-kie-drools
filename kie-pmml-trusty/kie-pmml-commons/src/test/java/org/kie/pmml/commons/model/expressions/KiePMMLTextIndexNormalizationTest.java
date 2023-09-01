package org.kie.pmml.commons.model.expressions;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.pmml.commons.model.expressions.KiePMMLTextIndex.DEFAULT_TOKENIZER;

public class KiePMMLTextIndexNormalizationTest {

    @Test
    void replaceSingleRow() {
        Map<String, Object> columnValues = new HashMap<>();
        columnValues.put("string", "interfaces?");
        columnValues.put("stem", "foo");
        columnValues.put("regex", "true");
        KiePMMLRow row = new KiePMMLRow(columnValues);
        KiePMMLInlineTable inlineTable = new KiePMMLInlineTable("inlineTable", Collections.emptyList(),
                                                                Collections.singletonList(row));
        KiePMMLTextIndexNormalization indexNormalization = KiePMMLTextIndexNormalization.builder("indexNormalization"
                        , Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable)
                .withIsCaseSensitive(false)
                .build();
        String text = "interfacea";
        String retrieved = indexNormalization.replace(text, true, 0, false, DEFAULT_TOKENIZER);
        assertThat(retrieved).isEqualTo("fooa");

        //---
        columnValues.put("string", "is|are|seem(ed|s?)|were?");
        columnValues.put("stem", "be");
        row = new KiePMMLRow(columnValues);
        inlineTable = new KiePMMLInlineTable("inlineTable", Collections.emptyList(), Collections.singletonList(row));
        indexNormalization = KiePMMLTextIndexNormalization.builder("indexNormalization", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable)
                .withIsCaseSensitive(false)
                .build();
        text = "Why they seem so ?";
        retrieved = indexNormalization.replace(text, true, 0, false, DEFAULT_TOKENIZER);
        assertThat(retrieved).isEqualTo("Why they be so ?");
    }

    @Test
    void replaceMultipleRows() {
        Map<String, Object> columnValues1 = new HashMap<>();
        columnValues1.put("string", "interfaces?");
        columnValues1.put("stem", "foo");
        columnValues1.put("regex", "true");
        KiePMMLRow row1 = new KiePMMLRow(columnValues1);
        Map<String, Object> columnValues2 = new HashMap<>();
        columnValues2.put("string", "is|are|seem(ed|s?)|were?");
        columnValues2.put("stem", "be");
        columnValues2.put("regex", "true");
        KiePMMLRow row2 = new KiePMMLRow(columnValues2);
        KiePMMLInlineTable inlineTable = new KiePMMLInlineTable("inlineTable", Collections.emptyList(), Arrays.asList(row1, row2));
        KiePMMLTextIndexNormalization indexNormalization = KiePMMLTextIndexNormalization.builder("indexNormalization", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable)
                .withIsCaseSensitive(false)
                .build();
        String text = "Why the interfacea seem so ?";
        String retrieved = indexNormalization.replace(text, true, 0, false, DEFAULT_TOKENIZER);
        assertThat(retrieved).isEqualTo("Why the fooa be so ?");
    }

    @Test
    void replaceMultipleRowsRecursive() {
        Map<String, Object> columnValues0 = new HashMap<>();
        columnValues0.put("string", "be");
        columnValues0.put("stem", "final");
        columnValues0.put("regex", "false");
        KiePMMLRow row0 = new KiePMMLRow(columnValues0);
        Map<String, Object> columnValues1 = new HashMap<>();
        columnValues1.put("string", "interfaces?");
        columnValues1.put("stem", "se");
        columnValues1.put("regex", "true");
        KiePMMLRow row1 = new KiePMMLRow(columnValues1);
        Map<String, Object> columnValues2 = new HashMap<>();
        columnValues2.put("string", "is|are|seem(ed|s?)|were?");
        columnValues2.put("stem", "be");
        columnValues2.put("regex", "true");
        KiePMMLRow row2 = new KiePMMLRow(columnValues2);
        KiePMMLInlineTable inlineTable = new KiePMMLInlineTable("inlineTable", Collections.emptyList(), Arrays.asList(row0, row1, row2));
        KiePMMLTextIndexNormalization indexNormalization = KiePMMLTextIndexNormalization.builder("indexNormalization", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable)
                .withIsCaseSensitive(false)
                .withRecursive(true)
                .build();
        String text = "Why they interfaceems so ?";
        String retrieved = indexNormalization.replace(text, true, 0, false, DEFAULT_TOKENIZER);
        assertThat(retrieved).isEqualTo("Why they final so ?");
    }
}