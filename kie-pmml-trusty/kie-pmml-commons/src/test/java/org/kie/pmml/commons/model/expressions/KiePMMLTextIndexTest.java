/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.pmml.commons.model.expressions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import org.apache.commons.text.similarity.LevenshteinDistance;
import org.junit.Test;
import org.kie.pmml.api.enums.COUNT_HITS;
import org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS;
import org.kie.pmml.commons.model.ProcessingDTO;
import org.kie.pmml.commons.model.tuples.KiePMMLNameValue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.AUGMENTED_NORMALIZED_TERM_FREQUENCY;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.BINARY;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.LOGARITHMIC;
import static org.kie.pmml.api.enums.LOCAL_TERM_WEIGHTS.TERM_FREQUENCY;

public class KiePMMLTextIndexTest {

    private static final String TERM = "brown fox";
    private static final String TEXT = "The quick browny fox 234 -. jumps over the lazy dog with another Brown Fox. " +
            "The brown fox runs away and to be with " +
            "another  ; : brown-foxy.";
    private static final String NOT_NORMALIZED_TEXT = "The quick blacky fox 234 -. jumps over the lazy dog with trotother Brown Fox. " +
            "The brown fox runs away and to be with " +
            "another  ; : again.";
    private static final String FIELD_NAME = "FIELD_NAME";

    @Test
    public void evaluateNoTextIndexNormalizations() {
        // <Constant>brown fox</Constant>
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME-1", Collections.emptyList(), TERM);
        List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME, TEXT));
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), kiePMMLNameValues);

        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            KiePMMLTextIndex kiePMMLTextIndex = KiePMMLTextIndex.builder(FIELD_NAME, Collections.emptyList(),
                                                                         kiePMMLConstant)
                    .withMaxLevenshteinDistance(2)
                    .withLocalTermWeights(localTermWeights)
                    .withIsCaseSensitive(true)
                    .build();
            assertEquals(expected, kiePMMLTextIndex.evaluate(processingDTO));
        });
    }

    @Test
    public void evaluateTextIndexNormalizations() {
        // <Constant>brown fox</Constant>
        final KiePMMLConstant kiePMMLConstant = new KiePMMLConstant("NAME-1", Collections.emptyList(), TERM);
        List<KiePMMLNameValue> kiePMMLNameValues = Collections.singletonList(new KiePMMLNameValue(FIELD_NAME, NOT_NORMALIZED_TEXT));
        ProcessingDTO processingDTO = new ProcessingDTO(Collections.emptyList(), Collections.emptyList(),
                                                        Collections.emptyList(), kiePMMLNameValues);

        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> {
            KiePMMLTextIndex kiePMMLTextIndex = KiePMMLTextIndex.builder(FIELD_NAME, Collections.emptyList(),
                                                                         kiePMMLConstant)
                    .withMaxLevenshteinDistance(2)
                    .withLocalTermWeights(localTermWeights)
                    .withIsCaseSensitive(true)
                    .withTextIndexNormalizations(getKiePMMLTextIndexNormalizations())
                    .build();
            assertEquals(expected, kiePMMLTextIndex.evaluate(processingDTO));
        });
    }

    @Test
    public void evaluateRawTokenize() {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(2);
        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(true,
                                                                                                          true,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "\\s+",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
        //---
        maxFrequency = 3;
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(false,
                                                                                                          true,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "\\s+",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
        //---
        frequency = 4.0;
        logarithmic = Math.log10(1 + frequency);
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(false,
                                                                                                          true,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "[\\s\\-]",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
    }

    @Test
    public void evaluateRawNoTokenize() {
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(2);
        Map<LOCAL_TERM_WEIGHTS, Double> expectedResults = new HashMap<>();
        double frequency = 3.0;
        double logarithmic = Math.log10(1 + frequency);
        int maxFrequency = 2;
        double augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(true,
                                                                                                          false,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "\\s+",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
        //---
        maxFrequency = 3;
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(false,
                                                                                                          false,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "\\s+",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
        //---
        frequency = 3.0;
        logarithmic = Math.log10(1 + frequency);
        augmentedNormalizedTermFrequency = 0.5 * (1 + (frequency / (double) maxFrequency)); // cast
        // for java:S2184
        expectedResults = new HashMap<>();
        expectedResults.put(TERM_FREQUENCY, frequency);
        expectedResults.put(BINARY, 1.0);
        expectedResults.put(LOGARITHMIC, logarithmic);
        expectedResults.put(AUGMENTED_NORMALIZED_TERM_FREQUENCY, augmentedNormalizedTermFrequency);
        expectedResults.forEach((localTermWeights, expected) -> assertEquals(expected,
                                                                             KiePMMLTextIndex.evaluateRaw(false,
                                                                                                          false,
                                                                                                          TERM,
                                                                                                          TEXT,
                                                                                                          "[\\s\\-]",
                                                                                                          localTermWeights,
                                                                                                          COUNT_HITS.ALL_HITS,
                                                                                                          levenshteinDistance), 0.0000001));
    }

    @Test
    public void evaluateAugmentedNormalizedTermFrequency() {
        Map<Integer, String> source = new HashMap<>();
        int maxFrequency = 23;
        source.put(maxFrequency, "aword");
        source.put(19, "anotherword");
        source.put(5, "adifferentword");
        source.put(3, "lastword");
        List<String> texts = new ArrayList<>();
        source.forEach((integer, s) -> IntStream.range(0, integer).forEach(i -> texts.add(s)));
        Collections.shuffle(texts);
        int calculatedLevenshteinDistance = 4;
        int binaryEvaluation = 1;
        double expected = 0.5 * (binaryEvaluation + (calculatedLevenshteinDistance / (double) maxFrequency)); // cast
        // for java:S2184
        assertEquals(expected,
                     KiePMMLTextIndex.evaluateAugmentedNormalizedTermFrequency(calculatedLevenshteinDistance, texts),
                     0.0);
    }

    @Test
    public void evaluateLevenshteinDistanceAllHits() {
        String wordSeparatorCharacterRE = "\\s+"; // brown-foxy does not match
        Pattern pattern = Pattern.compile(wordSeparatorCharacterRE);
        List<String> terms = KiePMMLTextIndex.splitText(TERM, pattern);
        List<String> texts = KiePMMLTextIndex.splitText(TEXT, pattern);
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(1, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(3, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
        //---
        wordSeparatorCharacterRE = "[\\s\\-]"; // brown-foxy match
        pattern = Pattern.compile(wordSeparatorCharacterRE);
        terms = KiePMMLTextIndex.splitText(TERM, pattern);
        texts = KiePMMLTextIndex.splitText(TEXT, pattern);
        levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(1, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(3, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(4, KiePMMLTextIndex.evaluateLevenshteinDistanceAllHits(levenshteinDistance, terms, texts));
    }

    @Test
    public void evaluateLevenshteinDistanceBestHits() {
        String wordSeparatorCharacterRE = "\\s+"; // brown-foxy does not match
        Pattern pattern = Pattern.compile(wordSeparatorCharacterRE);
        List<String> terms = KiePMMLTextIndex.splitText("The", pattern);
        List<String> texts = KiePMMLTextIndex.splitText(TEXT, pattern);
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
        //---
        wordSeparatorCharacterRE = "[\\s\\-]"; // brown-foxy match
        pattern = Pattern.compile(wordSeparatorCharacterRE);
        terms = KiePMMLTextIndex.splitText("The", pattern);
        texts = KiePMMLTextIndex.splitText(TEXT, pattern);
        levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistanceBestHits(levenshteinDistance, terms, texts));
    }

    @Test
    public void evaluateLevenshteinDistanceSplitText() {
        String toSearch = "brown fox";
        String toScan = "brown fox";
        LevenshteinDistance levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(0, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(0, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(0, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        toScan = "brown foxy";
        levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(-1, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(1, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(1, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        toScan = "browny foxy";
        levenshteinDistance = new LevenshteinDistance(0);
        assertEquals(-1, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(1);
        assertEquals(-1, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
        levenshteinDistance = new LevenshteinDistance(2);
        assertEquals(2, KiePMMLTextIndex.evaluateLevenshteinDistance(levenshteinDistance, toSearch, toScan));
    }

    @Test
    public void splitText() {
        final Pattern unwantedPattern = Pattern.compile("[^a-zA-Z0-9 ]");
        final Pattern wantedPattern = Pattern.compile("[a-zA-Z0-9]");
        Pattern pattern = Pattern.compile("\\s+");
        List<String> retrieved = KiePMMLTextIndex.splitText(TEXT, pattern);
        assertEquals(25, retrieved.size());
        retrieved.forEach(txt -> {
            assertFalse(unwantedPattern.matcher(txt).find());
            assertTrue(wantedPattern.matcher(txt).find());
        });
        pattern = Pattern.compile("[\\s\\-]");
        retrieved = KiePMMLTextIndex.splitText(TEXT, pattern);
        assertEquals(26, retrieved.size());
        retrieved.forEach(txt -> {
            assertFalse(unwantedPattern.matcher(txt).find());
            assertTrue(wantedPattern.matcher(txt).find());
        });
    }

    private List<KiePMMLTextIndexNormalization> getKiePMMLTextIndexNormalizations() {
        Map<String, Object> columnValues0 = new HashMap<>();
        columnValues0.put("string", "blacky");
        columnValues0.put("stem", "browny");
        columnValues0.put("regex", false);
        KiePMMLRow row0 = new KiePMMLRow(columnValues0);
        Map<String, Object> columnValues1 = new HashMap<>();
        columnValues1.put("string", "trot?");
        columnValues1.put("stem", "an");
        columnValues1.put("regex", true);
        KiePMMLRow row1 = new KiePMMLRow(columnValues1);
        KiePMMLInlineTable inlineTable1 = new KiePMMLInlineTable("inlineTable1", Collections.emptyList(), Arrays.asList(row0, row1));
        KiePMMLTextIndexNormalization indexNormalization1 = KiePMMLTextIndexNormalization.builder("indexNormalization1", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable1)
                .withRecursive(true)
                .build();
        Map<String, Object> columnValues2 = new HashMap<>();
        columnValues2.put("string", "again|is|are|seem(ed|s?)|were?");
        columnValues2.put("stem", "brown-foxy");
        columnValues2.put("regex", true);
        KiePMMLRow row2 = new KiePMMLRow(columnValues2);
        KiePMMLInlineTable inlineTable2 = new KiePMMLInlineTable("inlineTable2", Collections.emptyList(), Collections.singletonList(row2));
        KiePMMLTextIndexNormalization indexNormalization2 = KiePMMLTextIndexNormalization.builder("indexNormalization2", Collections.emptyList())
                .withKiePMMLInlineTable(inlineTable2)
                .withRecursive(true)
                .build();
        return Arrays.asList(indexNormalization1, indexNormalization2);
    }
}